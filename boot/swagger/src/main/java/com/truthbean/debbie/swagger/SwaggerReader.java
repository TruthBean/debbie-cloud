/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 *    Debbie is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.swagger;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.router.*;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import com.truthbean.common.mini.util.StringUtils;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.core.util.*;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.integration.ContextUtils;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.callbacks.Callback;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class SwaggerReader implements OpenApiReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerReader.class);

    private static final String DEFAULT_MEDIA_TYPE_VALUE = "*/*";
    private static final String DEFAULT_DESCRIPTION = "default response";

    protected OpenAPIConfiguration config;

    private OpenAPI openAPI;
    private Components components;
    private final Paths paths;
    private final Set<Tag> openApiTags;

    private final Set<RouterInfo> routerInfoSet;
    private final ClassLoader classLoader;

    public SwaggerReader(ClassLoader classLoader) {
        routerInfoSet = MvcRouterRegister.getRouterInfoSet();
        this.classLoader = classLoader;
        this.openAPI = new OpenAPI();
        paths = new Paths();
        openApiTags = new LinkedHashSet<>();
        components = new Components();

    }

    public SwaggerReader(OpenAPI openAPI, ClassLoader classLoader) {
        this(classLoader);
        setConfiguration(new SwaggerConfiguration().openAPI(openAPI));
    }

    public SwaggerReader(OpenAPIConfiguration openApiConfiguration, ClassLoader classLoader) {
        this(classLoader);
        setConfiguration(openApiConfiguration);
    }

    public OpenAPI getOpenAPI() {
        return openAPI;
    }

    /**
     * Scans a single class for Swagger annotations - does not invoke ReaderListeners
     */
    public OpenAPI read() {
        for (RouterInfo routerInfo : routerInfoSet) {
            RouterExecutor executor = routerInfo.getExecutor();
            if (executor instanceof MethodRouterExecutor) {
                Class<?> routerClass = ((MethodRouterExecutor) executor).getRouterClass();
                if (routerClass == DebbieSwaggerRouter.class) {
                    continue;
                }
            }
            read(routerInfo);
        }
        return openAPI;
    }

    /**
     * Scans a set of classes for both ReaderListeners and OpenAPI annotations. All found listeners will
     * be instantiated before any of the classes are scanned for OpenAPI annotations - so they can be invoked
     * accordingly.
     *
     * @param classes a set of classes to scan
     * @return the generated OpenAPI definition
     */
    public OpenAPI read(Set<Class<?>> classes) {
        Set<Class<?>> sortedClasses = new TreeSet<>((class1, class2) -> {
            if (class1.equals(class2)) {
                return 0;
            } else if (class1.isAssignableFrom(class2)) {
                return -1;
            } else if (class2.isAssignableFrom(class1)) {
                return 1;
            }
            return class1.getName().compareTo(class2.getName());
        });
        sortedClasses.addAll(classes);

        for (Class<?> cls : sortedClasses) {
            for (RouterInfo routerInfo : routerInfoSet) {
                RouterExecutor executor = routerInfo.getExecutor();
                if (executor instanceof MethodRouterExecutor) {
                    Class<?> routerClass = ((MethodRouterExecutor) executor).getRouterClass();
                    if (routerClass == cls) {
                        read(routerInfo);
                    }
                }
            }
        }

        return openAPI;
    }

    @Override
    public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
        if (openApiConfiguration != null) {
            this.config = ContextUtils.deepCopy(openApiConfiguration);
            if (openApiConfiguration.getOpenAPI() != null) {
                this.openAPI = this.config.getOpenAPI();
                if (this.openAPI.getComponents() != null) {
                    this.components = this.openAPI.getComponents();
                }
            }
        }
    }

    @Override
    public OpenAPI read(Set<Class<?>> classes, Map<String, Object> resources) {
        return read(classes);
    }

    public OpenAPI read(RouterInfo routerInfo) {

        Class<?> cls;
        Method method;
        RouterExecutor executor = routerInfo.getExecutor();
        if (executor instanceof MethodRouterExecutor) {
            cls = ((MethodRouterExecutor) executor).getRouterClass();
            method = ((MethodRouterExecutor) executor).getMethod();
        } else {
            return null;
        }

        Hidden hidden = cls.getAnnotation(Hidden.class);
        // class path

        if (hidden != null) {
            return openAPI;
        }

        io.swagger.v3.oas.annotations.responses.ApiResponse[] classResponses =
                ReflectionUtils.getRepeatableAnnotationsArray(cls, io.swagger.v3.oas.annotations.responses.ApiResponse.class);

        List<io.swagger.v3.oas.annotations.security.SecurityScheme> apiSecurityScheme =
                ReflectionUtils.getRepeatableAnnotations(cls, io.swagger.v3.oas.annotations.security.SecurityScheme.class);
        List<io.swagger.v3.oas.annotations.security.SecurityRequirement> apiSecurityRequirements =
                ReflectionUtils.getRepeatableAnnotations(cls, io.swagger.v3.oas.annotations.security.SecurityRequirement.class);

        ExternalDocumentation apiExternalDocs = ReflectionUtils.getAnnotation(cls, ExternalDocumentation.class);
        io.swagger.v3.oas.annotations.tags.Tag[] apiTags =
                ReflectionUtils.getRepeatableAnnotationsArray(cls, io.swagger.v3.oas.annotations.tags.Tag.class);
        io.swagger.v3.oas.annotations.servers.Server[] apiServers =
                ReflectionUtils.getRepeatableAnnotationsArray(cls, io.swagger.v3.oas.annotations.servers.Server.class);

        // OpenApiDefinition
        OpenAPIDefinition openAPIDefinition = ReflectionUtils.getAnnotation(cls, OpenAPIDefinition.class);
        RestRouter restRouter = ReflectionUtils.getAnnotation(cls, RestRouter.class);
        String routerTitle = null;

        if (openAPIDefinition != null) {

            // info
            AnnotationsUtils.getInfo(openAPIDefinition.info()).ifPresent(info -> openAPI.setInfo(info));

            // OpenApiDefinition security requirements
            SecurityParser
                    .getSecurityRequirements(openAPIDefinition.security())
                    .ifPresent(s -> openAPI.setSecurity(s));
            //
            // OpenApiDefinition external docs
            AnnotationsUtils
                    .getExternalDocumentation(openAPIDefinition.externalDocs())
                    .ifPresent(docs -> openAPI.setExternalDocs(docs));

            // OpenApiDefinition tags
            AnnotationsUtils
                    .getTags(openAPIDefinition.tags(), false)
                    .ifPresent(openApiTags::addAll);

            // OpenApiDefinition servers
            AnnotationsUtils
                    .getServers(openAPIDefinition.servers())
                    .ifPresent(servers -> openAPI.setServers(servers));

            // OpenApiDefinition extensions
            if (openAPIDefinition.extensions().length > 0) {
                openAPI.setExtensions(AnnotationsUtils
                        .getExtensions(openAPIDefinition.extensions()));
            }

        } else if (restRouter != null) {
            routerTitle = restRouter.title();
        }

        // class security schemes
        if (apiSecurityScheme != null) {
            for (io.swagger.v3.oas.annotations.security.SecurityScheme securitySchemeAnnotation : apiSecurityScheme) {
                var securityScheme = SecurityParser.getSecurityScheme(securitySchemeAnnotation);
                if (securityScheme.isPresent()) {
                    Map<String, SecurityScheme> securitySchemeMap = new HashMap<>();
                    if (StringUtils.isNotBlank(securityScheme.get().key)) {
                        securitySchemeMap.put(securityScheme.get().key, securityScheme.get().securityScheme);
                        if (components.getSecuritySchemes() != null && components.getSecuritySchemes().size() != 0) {
                            components.getSecuritySchemes().putAll(securitySchemeMap);
                        } else {
                            components.setSecuritySchemes(securitySchemeMap);
                        }
                    }
                }
            }
        }

        // class security requirements
        List<SecurityRequirement> classSecurityRequirements = new ArrayList<>();
        if (apiSecurityRequirements != null) {
            Optional<List<SecurityRequirement>> requirementsObject = SecurityParser.getSecurityRequirements(
                    apiSecurityRequirements.toArray(new io.swagger.v3.oas.annotations.security.SecurityRequirement[0])
            );
            if (requirementsObject.isPresent()) {
                classSecurityRequirements = requirementsObject.get();
            }
        }

        // class tags, consider only name to add to class operations
        final Set<String> classTags = new LinkedHashSet<>();
        if (apiTags != null) {
            AnnotationsUtils
                    .getTags(apiTags, false)
                    .ifPresent(tags -> tags.stream().map(Tag::getName).forEach(classTags::add));
        }
        if (routerTitle != null) {
            classTags.add(routerTitle);
        }

        // servers
        final List<io.swagger.v3.oas.models.servers.Server> classServers = new ArrayList<>();
        if (apiServers != null) {
            AnnotationsUtils.getServers(apiServers).ifPresent(classServers::addAll);
        }

        // class external docs
        var classExternalDocumentation = AnnotationsUtils.getExternalDocumentation(apiExternalDocs);


        JavaType classType = TypeFactory.defaultInstance().constructType(cls);
        BeanDescription bd = Json.mapper().getSerializationConfig().introspect(classType);

        final List<Parameter> globalParameters = new ArrayList<>();

        if (isOperationHidden(method)) {
            return processMethod(apiTags);
        }
        AnnotatedMethod annotatedMethod = bd.findMethod(method.getName(), method.getParameterTypes());
        MediaTypeInfo produces = routerInfo.getResponse().getResponseType();
        MediaTypeInfo consumes = routerInfo.getRequestType().info();

        if (ReflectionUtils.isOverriddenMethod(method, cls)) {
            return processMethod(apiTags);
        }

        List<RouterPathFragments> paths = routerInfo.getPaths();

        for (RouterPathFragments path : paths) {
            String operationPath = path.getRawPath();

            Map<String, String> regexMap = new LinkedHashMap<>();
            operationPath = PathUtils.parsePath(operationPath, regexMap);
            if (operationPath != null) {
                if (config != null && SwaggerReaderUtils.isIgnored(operationPath, config)) {
                    continue;
                }

                HttpMethod[] httpMethods = SwaggerReaderUtils.extractOperationMethods(method, OpenAPIExtensions.chain(), classLoader);
                if (httpMethods != null) {
                    for (HttpMethod httpMethod : httpMethods) {
                        if (httpMethod == null) {
                            Type returnType = method.getGenericReturnType();
                            if (annotatedMethod != null && annotatedMethod.getType() != null) {
                                returnType = annotatedMethod.getType();
                            }

                            if (shouldIgnoreClass(returnType.getTypeName())) {
                                continue;
                            }
                        }

                        io.swagger.v3.oas.annotations.Operation apiOperation =
                                ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.Operation.class);
                        JsonView jsonViewAnnotation;
                        JsonView jsonViewAnnotationForRequestBody;
                        if (apiOperation != null && apiOperation.ignoreJsonView()) {
                            jsonViewAnnotation = null;
                            jsonViewAnnotationForRequestBody = null;
                        } else {
                            jsonViewAnnotation = ReflectionUtils.getAnnotation(method, JsonView.class);
                            /* If one and only one exists, use the @JsonView annotation from the method parameter annotated
                            with @RequestBody. Otherwise fall back to the @JsonView annotation for the method itself. */
                            jsonViewAnnotationForRequestBody = (JsonView) Arrays.stream(ReflectionUtils.getParameterAnnotations(method))
                                    .filter(arr ->
                                            Arrays.stream(arr)
                                                    .anyMatch(annotation ->
                                                            annotation.annotationType()
                                                                    .equals(io.swagger.v3.oas.annotations.parameters.RequestBody.class)
                                                    )
                                    ).flatMap(Arrays::stream)
                                    .filter(annotation ->
                                            annotation.annotationType()
                                                    .equals(JsonView.class)
                                    ).reduce((a, b) -> {
                                        // todo check
                                        return null;
                                    })
                                    .orElse(jsonViewAnnotation);
                        }

                        Operation operation = parseMethod(method, globalParameters, produces, consumes,
                                classSecurityRequirements, classExternalDocumentation, classTags, classServers,
                                jsonViewAnnotation, classResponses, annotatedMethod);
                        if (operation != null) {

                            List<Parameter> operationParameters = new ArrayList<>();
                            List<Parameter> formParameters = new ArrayList<>();
                            Annotation[][] paramAnnotations = ReflectionUtils.getParameterAnnotations(method);
                            if (annotatedMethod == null) {
                                // annotatedMethod not null only when method with 0-2 parameters
                                Type[] genericParameterTypes = method.getGenericParameterTypes();
                                for (int i = 0; i < genericParameterTypes.length; i++) {
                                    final Type type = TypeFactory.defaultInstance().constructType(genericParameterTypes[i], cls);
                                    io.swagger.v3.oas.annotations.Parameter paramAnnotation =
                                            AnnotationsUtils.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class, paramAnnotations[i]);
                                    Type paramType = ParameterProcessor.getParameterType(paramAnnotation, true);
                                    if (paramType == null) {
                                        paramType = type;
                                    } else {
                                        if (!(paramType instanceof Class)) {
                                            paramType = type;
                                        }
                                    }
                                    ResolvedParameter resolvedParameter = getParameters(paramType, Arrays.asList(paramAnnotations[i]), operation, consumes, jsonViewAnnotation);
                                    operationParameters.addAll(resolvedParameter.getParameters());
                                    // collect params to use together as request Body
                                    formParameters.addAll(resolvedParameter.getFormParameters());
                                    if (resolvedParameter.getRequestBody() != null) {
                                        processRequestBody(resolvedParameter.getRequestBody(), operation, produces,
                                                operationParameters, paramAnnotations[i], type, jsonViewAnnotationForRequestBody);
                                    }
                                }
                            } else {
                                for (int i = 0; i < annotatedMethod.getParameterCount(); i++) {
                                    AnnotatedParameter param = annotatedMethod.getParameter(i);
                                    final Type type = TypeFactory.defaultInstance().constructType(param.getParameterType(), cls);
                                    io.swagger.v3.oas.annotations.Parameter paramAnnotation = AnnotationsUtils.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class, paramAnnotations[i]);
                                    Type paramType = ParameterProcessor.getParameterType(paramAnnotation, true);
                                    if (paramType == null) {
                                        paramType = type;
                                    } else {
                                        if (!(paramType instanceof Class)) {
                                            paramType = type;
                                        }
                                    }
                                    ResolvedParameter resolvedParameter = getParameters(paramType, Arrays.asList(paramAnnotations[i]), operation, consumes, jsonViewAnnotation);
                                    operationParameters.addAll(resolvedParameter.getParameters());
                                    // collect params to use together as request Body
                                    formParameters.addAll(resolvedParameter.getFormParameters());
                                    if (resolvedParameter.getRequestBody() != null) {
                                        processRequestBody(resolvedParameter.getRequestBody(), operation, consumes,
                                                operationParameters, paramAnnotations[i], type, jsonViewAnnotationForRequestBody);
                                    }
                                }
                            }
                            // if we have form parameters, need to merge them into single schema and use as request body..
                            if (formParameters.size() > 0) {
                                Schema mergedSchema = new ObjectSchema();
                                for (Parameter formParam : formParameters) {
                                    mergedSchema.addProperties(formParam.getName(), formParam.getSchema());
                                    if (null != formParam.getRequired() && formParam.getRequired()) {
                                        mergedSchema.addRequiredItem(formParam.getName());
                                    }
                                }
                                Parameter merged = new Parameter().schema(mergedSchema);
                                processRequestBody(merged, operation, consumes, operationParameters, new Annotation[0],
                                        null, jsonViewAnnotationForRequestBody);

                            }
                            if (operationParameters.size() > 0) {
                                for (Parameter operationParameter : operationParameters) {
                                    operation.addParametersItem(operationParameter);
                                }
                            }

                            final Iterator<OpenAPIExtension> chain = OpenAPIExtensions.chain();
                            if (chain.hasNext()) {
                                final OpenAPIExtension extension = chain.next();
                                extension.decorateOperation(operation, method, chain);
                            }

                            PathItem pathItemObject;
                            if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
                                pathItemObject = openAPI.getPaths().get(operationPath);
                            } else {
                                pathItemObject = new PathItem();
                            }

                            if (httpMethod == null) {
                                return processMethod(apiTags);
                            }
                            setPathItemOperation(pathItemObject, httpMethod, operation);

                            this.paths.addPathItem(operationPath, pathItemObject);
                            if (openAPI.getPaths() != null) {
                                this.paths.putAll(openAPI.getPaths());
                            }

                            openAPI.setPaths(this.paths);
                        }
                    }
                }
            }
        }

        return processMethod(apiTags);
    }

    private OpenAPI processMethod(io.swagger.v3.oas.annotations.tags.Tag[] apiTags) {
        // if no components object is defined in openApi instance passed by client, set openAPI.components to resolved components (if not empty)
        if (!isEmptyComponents(components) && openAPI.getComponents() == null) {
            openAPI.setComponents(components);
        }

        // add tags from class to definition tags
        AnnotationsUtils
                .getTags(apiTags, true).ifPresent(openApiTags::addAll);

        handleTag();

        return openAPI;
    }

    private OpenAPI processMethod(String[] tags) {
        for (String tag : tags) {
            Tag t = new Tag();
            t.setName(tag);
            openApiTags.add(t);
        }
        return openAPI;
    }

    private void handleTag() {
        if (!openApiTags.isEmpty()) {
            Set<Tag> tagsSet = new LinkedHashSet<>();
            if (openAPI.getTags() != null) {
                for (Tag tag : openAPI.getTags()) {
                    if (tagsSet.stream().noneMatch(t -> t.getName().equals(tag.getName()))) {
                        tagsSet.add(tag);
                    }
                }
            }
            for (Tag tag : openApiTags) {
                if (tagsSet.stream().noneMatch(t -> t.getName().equals(tag.getName()))) {
                    tagsSet.add(tag);
                }
            }
            openAPI.setTags(new ArrayList<>(tagsSet));
        }
    }

    protected Content processContent(Content content, Schema schema, MediaTypeInfo consumes) {
        if (content == null) {
            content = new Content();
        }
        if (consumes != null) {
            setMediaTypeToContent(schema, content, consumes.toString());
        } else {
            setMediaTypeToContent(schema, content, DEFAULT_MEDIA_TYPE_VALUE);
        }
        return content;
    }

    protected void processRequestBody(Parameter requestBodyParameter, Operation operation,
                                      MediaTypeInfo consumes,
                                      List<Parameter> operationParameters,
                                      Annotation[] paramAnnotations, Type type,
                                      JsonView jsonViewAnnotation) {

        io.swagger.v3.oas.annotations.parameters.RequestBody requestBodyAnnotation = getRequestBody(Arrays.asList(paramAnnotations));
        if (requestBodyAnnotation != null) {
            Optional<RequestBody> optionalRequestBody = OperationParser.getRequestBody(requestBodyAnnotation, consumes, components, jsonViewAnnotation);
            if (optionalRequestBody.isPresent()) {
                RequestBody requestBody = optionalRequestBody.get();
                if (StringUtils.isBlank(requestBody.get$ref()) &&
                        (requestBody.getContent() == null || requestBody.getContent().isEmpty())) {
                    if (requestBodyParameter.getSchema() != null) {
                        Content content = processContent(requestBody.getContent(), requestBodyParameter.getSchema(), consumes);
                        requestBody.setContent(content);
                    }
                } else if (StringUtils.isBlank(requestBody.get$ref()) &&
                        requestBody.getContent() != null &&
                        !requestBody.getContent().isEmpty()) {
                    if (requestBodyParameter.getSchema() != null) {
                        for (MediaType mediaType : requestBody.getContent().values()) {
                            if (mediaType.getSchema() == null) {
                                if (requestBodyParameter.getSchema() == null) {
                                    mediaType.setSchema(new Schema());
                                } else {
                                    mediaType.setSchema(requestBodyParameter.getSchema());
                                }
                            }
                            if (StringUtils.isBlank(mediaType.getSchema().getType())) {
                                mediaType.getSchema().setType(requestBodyParameter.getSchema().getType());
                            }
                        }
                    }
                }
                operation.setRequestBody(requestBody);
            }
        } else {
            if (operation.getRequestBody() == null) {
                boolean isRequestBodyEmpty = true;
                RequestBody requestBody = new RequestBody();
                if (StringUtils.isNotBlank(requestBodyParameter.get$ref())) {
                    requestBody.set$ref(requestBodyParameter.get$ref());
                    isRequestBodyEmpty = false;
                }
                if (StringUtils.isNotBlank(requestBodyParameter.getDescription())) {
                    requestBody.setDescription(requestBodyParameter.getDescription());
                    isRequestBodyEmpty = false;
                }
                if (Boolean.TRUE.equals(requestBodyParameter.getRequired())) {
                    requestBody.setRequired(requestBodyParameter.getRequired());
                    isRequestBodyEmpty = false;
                }

                if (requestBodyParameter.getSchema() != null) {
                    Content content = processContent(null, requestBodyParameter.getSchema(), consumes);
                    requestBody.setContent(content);
                    isRequestBodyEmpty = false;
                }
                if (!isRequestBodyEmpty) {
                    // requestBody.setExtensions(extensions);
                    operation.setRequestBody(requestBody);
                }
            }
        }
    }

    private io.swagger.v3.oas.annotations.parameters.RequestBody getRequestBody(List<Annotation> annotations) {
        if (annotations == null) {
            return null;
        }
        for (Annotation a : annotations) {
            if (a instanceof io.swagger.v3.oas.annotations.parameters.RequestBody) {
                return (io.swagger.v3.oas.annotations.parameters.RequestBody) a;
            }
        }
        return null;
    }

    private void setMediaTypeToContent(Schema schema, Content content, String value) {
        MediaType mediaTypeObject = new MediaType();
        mediaTypeObject.setSchema(schema);
        content.addMediaType(value, mediaTypeObject);
    }

    protected Operation parseMethod(Method method, List<Parameter> globalParameters, MediaTypeInfo produces, MediaTypeInfo consumes,
            List<SecurityRequirement> classSecurityRequirements, Optional<io.swagger.v3.oas.models.ExternalDocumentation> classExternalDocs,
            Set<String> classTags, List<io.swagger.v3.oas.models.servers.Server> classServers, JsonView jsonViewAnnotation,
            io.swagger.v3.oas.annotations.responses.ApiResponse[] classResponses, AnnotatedMethod annotatedMethod) {
        Operation operation = new Operation();

        io.swagger.v3.oas.annotations.Operation apiOperation = ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.Operation.class);

        List<io.swagger.v3.oas.annotations.security.SecurityRequirement> apiSecurity =
                ReflectionUtils.getRepeatableAnnotations(method, io.swagger.v3.oas.annotations.security.SecurityRequirement.class);
        List<io.swagger.v3.oas.annotations.callbacks.Callback> apiCallbacks =
                ReflectionUtils.getRepeatableAnnotations(method, io.swagger.v3.oas.annotations.callbacks.Callback.class);
        List<Server> apiServers = ReflectionUtils.getRepeatableAnnotations(method, Server.class);
        List<io.swagger.v3.oas.annotations.tags.Tag> apiTags =
                ReflectionUtils.getRepeatableAnnotations(method, io.swagger.v3.oas.annotations.tags.Tag.class);
        List<io.swagger.v3.oas.annotations.Parameter> apiParameters =
                ReflectionUtils.getRepeatableAnnotations(method, io.swagger.v3.oas.annotations.Parameter.class);
        List<io.swagger.v3.oas.annotations.responses.ApiResponse> apiResponses =
                ReflectionUtils.getRepeatableAnnotations(method, io.swagger.v3.oas.annotations.responses.ApiResponse.class);
        io.swagger.v3.oas.annotations.parameters.RequestBody apiRequestBody =
                ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.parameters.RequestBody.class);

        ExternalDocumentation apiExternalDocumentation = ReflectionUtils.getAnnotation(method, ExternalDocumentation.class);

        // callbacks
        Map<String, Callback> callbacks = new LinkedHashMap<>();

        if (apiCallbacks != null) {
            for (io.swagger.v3.oas.annotations.callbacks.Callback methodCallback : apiCallbacks) {
                Map<String, Callback> currentCallbacks = getCallbacks(methodCallback, produces, consumes, jsonViewAnnotation);
                callbacks.putAll(currentCallbacks);
            }
        }
        if (callbacks.size() > 0) {
            operation.setCallbacks(callbacks);
        }

        // security
        classSecurityRequirements.forEach(operation::addSecurityItem);
        if (apiSecurity != null) {
            Optional<List<SecurityRequirement>> requirementsObject =
                    SecurityParser.getSecurityRequirements(apiSecurity.toArray(new io.swagger.v3.oas.annotations.security.SecurityRequirement[0]));
            requirementsObject
                    .ifPresent(securityRequirements -> securityRequirements.stream()
                            .filter(r -> operation.getSecurity() == null || !operation.getSecurity().contains(r))
                            .forEach(operation::addSecurityItem));
        }

        // servers
        if (classServers != null) {
            classServers.forEach(operation::addServersItem);
        }

        if (apiServers != null) {
            AnnotationsUtils
                    .getServers(apiServers.toArray(new Server[0]))
                    .ifPresent(servers -> servers.forEach(operation::addServersItem));
        }

        // external docs
        AnnotationsUtils.getExternalDocumentation(apiExternalDocumentation).ifPresent(operation::setExternalDocs);

        // method tags
        if (apiTags != null && !apiTags.isEmpty()) {
            apiTags.stream()
                    .filter(t -> operation.getTags() == null || (operation.getTags() != null && !operation.getTags().contains(t.name())))
                    .map(io.swagger.v3.oas.annotations.tags.Tag::name)
                    .forEach(operation::addTagsItem);
            AnnotationsUtils
                    .getTags(apiTags.toArray(new io.swagger.v3.oas.annotations.tags.Tag[0]), true)
                    .ifPresent(openApiTags::addAll);
        }

        // parameters
        if (globalParameters != null) {
            for (Parameter globalParameter : globalParameters) {
                operation.addParametersItem(globalParameter);
            }
        }
        if (apiParameters != null) {
            getParametersListFromAnnotation(apiParameters.toArray(new io.swagger.v3.oas.annotations.Parameter[0]), consumes, operation, jsonViewAnnotation)
                    .ifPresent(p -> p.forEach(operation::addParametersItem));
        }

        // RequestBody in Method
        if (apiRequestBody != null && operation.getRequestBody() == null) {
            OperationParser.getRequestBody(apiRequestBody, consumes, components, jsonViewAnnotation).ifPresent(operation::setRequestBody);
        }

        // operation id
        if (StringUtils.isBlank(operation.getOperationId())) {
            operation.setOperationId(getOperationId(method.getName()));
        }

        // classResponses
        if (classResponses != null && classResponses.length > 0) {
            OperationParser.getApiResponses(classResponses, produces, components, jsonViewAnnotation)
                    .ifPresent(responses -> {
                        if (operation.getResponses() == null) {
                            operation.setResponses(responses);
                        } else {
                            responses.forEach(operation.getResponses()::addApiResponse);
                        }
                    });
        }

        if (apiOperation != null) {
            setOperationObjectFromApiOperationAnnotation(operation, apiOperation, produces, consumes, jsonViewAnnotation);
        }

        // apiResponses
        if (apiResponses != null && apiResponses.size() > 0) {
            OperationParser.getApiResponses(
                    apiResponses.toArray(new io.swagger.v3.oas.annotations.responses.ApiResponse[0]),
                    produces,
                    components,
                    jsonViewAnnotation
            ).ifPresent(responses -> {
                if (operation.getResponses() == null) {
                    operation.setResponses(responses);
                } else {
                    responses.forEach(operation.getResponses()::addApiResponse);
                }
            });
        }

        // class tags after tags defined as field of @Operation
        if (classTags != null && !classTags.isEmpty()) {
            classTags.stream()
                    .filter(t ->
                            operation.getTags() == null || operation.getTags().isEmpty()
                                    || (operation.getTags() != null && !operation.getTags().contains(t)))
                    .forEach(operation::addTagsItem);
        }

        // external docs of class if not defined in annotation of method or as field of Operation annotation
        if (operation.getExternalDocs() == null) {
            classExternalDocs.ifPresent(operation::setExternalDocs);
        }

        // handle return type, add as response in case.
        Type returnType = method.getGenericReturnType();

        if (annotatedMethod != null && annotatedMethod.getType() != null) {
            returnType = annotatedMethod.getType();
        }

        final Class<?> subResource = getSubResourceWithJaxRsSubresourceLocatorSpecs(method);
        if (!shouldIgnoreClass(returnType.getTypeName()) && !method.getGenericReturnType().equals(subResource)) {
            ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                    .resolveAsResolvedSchema(new AnnotatedType(returnType).resolveAsRef(true).jsonViewAnnotation(jsonViewAnnotation));
            if (resolvedSchema.schema != null) {
                Schema returnTypeSchema = resolvedSchema.schema;
                Content content = new Content();
                MediaType mediaType = new MediaType().schema(returnTypeSchema);
                AnnotationsUtils.applyTypes(new String[0],
                        produces == null ? new String[0] : new String[]{produces.toString()}, content, mediaType);
                if (operation.getResponses() == null) {
                    operation.responses(
                            new ApiResponses()._default(
                                    new ApiResponse().description(DEFAULT_DESCRIPTION)
                                            .content(content)
                            )
                    );
                }
                if (operation.getResponses().getDefault() != null &&
                        StringUtils.isBlank(operation.getResponses().getDefault().get$ref())) {
                    if (operation.getResponses().getDefault().getContent() == null) {
                        operation.getResponses().getDefault().content(content);
                    } else {
                        for (String key : operation.getResponses().getDefault().getContent().keySet()) {
                            if (operation.getResponses().getDefault().getContent().get(key).getSchema() == null) {
                                operation.getResponses().getDefault().getContent().get(key).setSchema(returnTypeSchema);
                            }
                        }
                    }
                }
                Map<String, Schema> schemaMap = resolvedSchema.referencedSchemas;
                if (schemaMap != null) {
                    schemaMap.forEach((key, schema) -> components.addSchemas(key, schema));
                }

            }
        }
        if (operation.getResponses() == null || operation.getResponses().isEmpty()) {
            Content content = new Content();
            MediaType mediaType = new MediaType();
            AnnotationsUtils.applyTypes(new String[0],
                    produces == null ? new String[0] : new String[]{produces.toString()}, content, mediaType);

            ApiResponse apiResponseObject = new ApiResponse().description(DEFAULT_DESCRIPTION).content(content);
            operation.setResponses(new ApiResponses()._default(apiResponseObject));
        }

        return operation;
    }

    private boolean shouldIgnoreClass(String className) {
        if (StringUtils.isBlank(className)) {
            return true;
        }
        boolean ignore = false;
        String rawClassName = className;
        if (rawClassName.startsWith("[")) {
            // jackson JavaType
            rawClassName = className.replace("[simple type, class ", "");
            rawClassName = rawClassName.substring(0, rawClassName.length() - 1);
        }
        // ignore = rawClassName.startsWith("javax.ws.rs.");
        ignore = ignore || "void".equalsIgnoreCase(rawClassName);
        ignore = ignore || ModelConverters.getInstance().isRegisteredAsSkippedClass(rawClassName);
        return ignore;
    }

    private Map<String, Callback> getCallbacks(io.swagger.v3.oas.annotations.callbacks.Callback apiCallback,
            MediaTypeInfo produces, MediaTypeInfo consumes, JsonView jsonViewAnnotation) {
        Map<String, Callback> callbackMap = new HashMap<>();
        if (apiCallback == null) {
            return callbackMap;
        }

        Callback callbackObject = new Callback();
        if (StringUtils.isNotBlank(apiCallback.ref())) {
            callbackObject.set$ref(apiCallback.ref());
            callbackMap.put(apiCallback.name(), callbackObject);
            return callbackMap;
        }
        PathItem pathItemObject = new PathItem();
        for (io.swagger.v3.oas.annotations.Operation callbackOperation : apiCallback.operation()) {
            Operation callbackNewOperation = new Operation();
            setOperationObjectFromApiOperationAnnotation(callbackNewOperation, callbackOperation, produces, consumes, jsonViewAnnotation);
            setPathItemOperation(pathItemObject, HttpMethod.valueOf(callbackOperation.method().toUpperCase()), callbackNewOperation);
        }

        callbackObject.addPathItem(apiCallback.callbackUrlExpression(), pathItemObject);
        callbackMap.put(apiCallback.name(), callbackObject);

        return callbackMap;
    }

    private void setPathItemOperation(PathItem pathItemObject, HttpMethod method, Operation operation) {
        switch (method) {
            case POST:
                pathItemObject.post(operation);
                break;
            case GET:
                pathItemObject.get(operation);
                break;
            case DELETE:
                pathItemObject.delete(operation);
                break;
            case PUT:
                pathItemObject.put(operation);
                break;
            case PATCH:
                pathItemObject.patch(operation);
                break;
            case TRACE:
                pathItemObject.trace(operation);
                break;
            case HEAD:
                pathItemObject.head(operation);
                break;
            case OPTIONS:
                pathItemObject.options(operation);
                break;
            default:
                // Do nothing here
                break;
        }
    }

    private void setOperationObjectFromApiOperationAnnotation(Operation operation, io.swagger.v3.oas.annotations.Operation apiOperation,
                                                              MediaTypeInfo produces, MediaTypeInfo consumes, JsonView jsonViewAnnotation) {
        if (StringUtils.isNotBlank(apiOperation.summary())) {
            operation.setSummary(apiOperation.summary());
        }
        if (StringUtils.isNotBlank(apiOperation.description())) {
            operation.setDescription(apiOperation.description());
        }
        if (StringUtils.isNotBlank(apiOperation.operationId())) {
            operation.setOperationId(getOperationId(apiOperation.operationId()));
        }
        if (apiOperation.deprecated()) {
            operation.setDeprecated(apiOperation.deprecated());
        }

        SwaggerReaderUtils.getStringListFromStringArray(apiOperation.tags()).ifPresent(tags -> tags.stream()
                .filter(t -> operation.getTags() == null || (operation.getTags() != null && !operation.getTags().contains(t)))
                .forEach(operation::addTagsItem));

        // if not set in root annotation
        if (operation.getExternalDocs() == null) {
            AnnotationsUtils.getExternalDocumentation(apiOperation.externalDocs()).ifPresent(operation::setExternalDocs);
        }

        OperationParser.getApiResponses(apiOperation.responses(), produces, components, jsonViewAnnotation).ifPresent(responses -> {
            if (operation.getResponses() == null) {
                operation.setResponses(responses);
            } else {
                responses.forEach(operation.getResponses()::addApiResponse);
            }
        });
        AnnotationsUtils.getServers(apiOperation.servers()).ifPresent(servers -> servers.forEach(operation::addServersItem));

        getParametersListFromAnnotation(apiOperation.parameters(), consumes, operation, jsonViewAnnotation)
                .ifPresent(p -> p.forEach(operation::addParametersItem));

        // security
        Optional<List<SecurityRequirement>> requirementsObject = SecurityParser.getSecurityRequirements(apiOperation.security());
        requirementsObject
                .ifPresent(securityRequirements -> securityRequirements.stream()
                        .filter(r -> operation.getSecurity() == null || !operation.getSecurity().contains(r))
                        .forEach(operation::addSecurityItem));

        // RequestBody in Operation
        var requestBody = apiOperation.requestBody();
        if (operation.getRequestBody() == null) {
            OperationParser
                    .getRequestBody(requestBody, consumes, components, jsonViewAnnotation)
                    .ifPresent(operation::setRequestBody);
        }

        // Extensions in Operation
        if (apiOperation.extensions().length > 0) {
            Map<String, Object> extensions = AnnotationsUtils.getExtensions(apiOperation.extensions());
            if (!extensions.isEmpty()) {
                for (String ext : extensions.keySet()) {
                    operation.addExtension(ext, extensions.get(ext));
                }
            }
        }
    }

    protected String getOperationId(String operationId) {
        boolean operationIdUsed = existOperationId(operationId);
        String operationIdToFind = null;
        int counter = 0;
        while (operationIdUsed) {
            operationIdToFind = String.format("%s_%d", operationId, ++counter);
            operationIdUsed = existOperationId(operationIdToFind);
        }
        if (operationIdToFind != null) {
            operationId = operationIdToFind;
        }
        return operationId;
    }

    private boolean existOperationId(String operationId) {
        if (openAPI == null) {
            return false;
        }
        if (openAPI.getPaths() == null || openAPI.getPaths().isEmpty()) {
            return false;
        }
        for (PathItem path : openAPI.getPaths().values()) {
            Set<String> pathOperationIds = extractOperationIdFromPathItem(path);
            if (pathOperationIds.contains(operationId)) {
                return true;
            }
        }
        return false;
    }

    protected Optional<List<Parameter>> getParametersListFromAnnotation(
            io.swagger.v3.oas.annotations.Parameter[] parameters, MediaTypeInfo consumes, Operation operation,
            JsonView jsonViewAnnotation) {
        if (parameters == null) {
            return Optional.empty();
        }
        List<Parameter> parametersObject = new ArrayList<>();
        for (io.swagger.v3.oas.annotations.Parameter parameter : parameters) {

            ResolvedParameter resolvedParameter = getParameters(ParameterProcessor.getParameterType(parameter),
                    Collections.singletonList(parameter), operation, consumes, jsonViewAnnotation);
            parametersObject.addAll(resolvedParameter.getParameters());
        }
        if (parametersObject.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(parametersObject);
    }

    protected ResolvedParameter getParameters(Type type, List<Annotation> annotations, Operation operation,
                                              MediaTypeInfo consumes, JsonView jsonViewAnnotation) {
        final Iterator<OpenAPIExtension> chain = OpenAPIExtensions.chain();
        if (!chain.hasNext()) {
            return new ResolvedParameter();
        }
        LOGGER.debug("getParameters for {}", type);
        Set<Type> typesToSkip = new HashSet<>();
        final OpenAPIExtension extension = chain.next();
        LOGGER.debug("trying extension {}", extension);

        return extension.extractParameters(annotations, type, typesToSkip, components, consumes, true, jsonViewAnnotation, chain);
    }

    private Set<String> extractOperationIdFromPathItem(PathItem path) {
        Set<String> ids = new HashSet<>();
        if (path.getGet() != null && StringUtils.isNotBlank(path.getGet().getOperationId())) {
            ids.add(path.getGet().getOperationId());
        }
        if (path.getPost() != null && StringUtils.isNotBlank(path.getPost().getOperationId())) {
            ids.add(path.getPost().getOperationId());
        }
        if (path.getPut() != null && StringUtils.isNotBlank(path.getPut().getOperationId())) {
            ids.add(path.getPut().getOperationId());
        }
        if (path.getDelete() != null && StringUtils.isNotBlank(path.getDelete().getOperationId())) {
            ids.add(path.getDelete().getOperationId());
        }
        if (path.getOptions() != null && StringUtils.isNotBlank(path.getOptions().getOperationId())) {
            ids.add(path.getOptions().getOperationId());
        }
        if (path.getHead() != null && StringUtils.isNotBlank(path.getHead().getOperationId())) {
            ids.add(path.getHead().getOperationId());
        }
        if (path.getPatch() != null && StringUtils.isNotBlank(path.getPatch().getOperationId())) {
            ids.add(path.getPatch().getOperationId());
        }
        return ids;
    }

    private boolean isEmptyComponents(Components components) {
        if (components == null) {
            return true;
        }
        if (components.getSchemas() != null && components.getSchemas().size() > 0) {
            return false;
        }
        if (components.getSecuritySchemes() != null && components.getSecuritySchemes().size() > 0) {
            return false;
        }
        if (components.getCallbacks() != null && components.getCallbacks().size() > 0) {
            return false;
        }
        if (components.getExamples() != null && components.getExamples().size() > 0) {
            return false;
        }
        if (components.getExtensions() != null && components.getExtensions().size() > 0) {
            return false;
        }
        if (components.getHeaders() != null && components.getHeaders().size() > 0) {
            return false;
        }
        if (components.getLinks() != null && components.getLinks().size() > 0) {
            return false;
        }
        if (components.getParameters() != null && components.getParameters().size() > 0) {
            return false;
        }
        if (components.getRequestBodies() != null && components.getRequestBodies().size() > 0) {
            return false;
        }
        return components.getResponses() == null || components.getResponses().size() <= 0;
    }

    protected boolean isOperationHidden(Method method) {
        io.swagger.v3.oas.annotations.Operation apiOperation = ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.Operation.class);
        if (apiOperation != null && apiOperation.hidden()) {
            return true;
        }
        Hidden hidden = method.getAnnotation(Hidden.class);
        if (hidden != null) {
            return true;
        }
        return config != null && !Boolean.TRUE.equals(config.isReadAllResources()) && apiOperation == null;
    }

    protected Class<?> getSubResourceWithJaxRsSubresourceLocatorSpecs(Method method) {
        final Class<?> rawType = method.getReturnType();
        final Class<?> type;
        if (Class.class.equals(rawType)) {
            type = getClassArgument(method.getGenericReturnType());
            if (type == null) {
                return null;
            }
        } else {
            type = rawType;
        }

        if (RouterAnnotationInfoParser.getRouterAnnotation(method, classLoader) != null) {
            return type;
        }
        return null;
    }

    private static Class<?> getClassArgument(Type cls) {
        if (cls instanceof ParameterizedType) {
            final ParameterizedType parameterized = (ParameterizedType) cls;
            final Type[] args = parameterized.getActualTypeArguments();
            if (args.length != 1) {
                LOGGER.error("Unexpected class definition: {}", cls);
                return null;
            }
            final Type first = args[0];
            if (first instanceof Class) {
                return (Class<?>) first;
            } else {
                return null;
            }
        } else {
            LOGGER.error("Unknown class definition: {}", cls);
            return null;
        }
    }
}