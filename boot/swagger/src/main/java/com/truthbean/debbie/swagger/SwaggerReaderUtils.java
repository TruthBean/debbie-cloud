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
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.mvc.router.RouterAnnotationInfo;
import com.truthbean.debbie.mvc.router.RouterAnnotationInfoParser;
import com.truthbean.debbie.mvc.router.RouterAnnotationParser;
import com.truthbean.common.mini.util.StringUtils;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class SwaggerReaderUtils {
    private static final String PATH_DELIMITER = "/";

    /**
     * Collects field-level parameters from class.
     *
     * @param cls        is a class for collecting
     * @param components
     * @return the collection of supported parameters
     */
    public static List<Parameter> collectFieldParameters(Class<?> cls, Components components, MediaTypeInfo consumes, JsonView jsonViewAnnotation) {
        final List<Parameter> parameters = new ArrayList<>();
        for (Field field : ReflectionUtils.getDeclaredFields(cls)) {
            final List<Annotation> annotations = Arrays.asList(field.getAnnotations());
            final Type genericType = field.getGenericType();
            parameters.addAll(collectParameters(genericType, annotations, components, consumes, jsonViewAnnotation));
        }
        return parameters;
    }

    private static List<Parameter> collectParameters(Type type, List<Annotation> annotations, Components components, MediaTypeInfo consumes, JsonView jsonViewAnnotation) {
        final Iterator<OpenAPIExtension> chain = OpenAPIExtensions.chain();
        return chain.hasNext()
                ?
                chain.next().extractParameters(annotations, type, new HashSet<>(), components, consumes, false, jsonViewAnnotation, chain).getParameters()
                :
                Collections.emptyList();
    }

    public static Optional<List<String>> getStringListFromStringArray(String[] array) {
        if (array == null) {
            return Optional.empty();
        }
        List<String> list = new ArrayList<>();
        boolean isEmpty = true;
        for (String value : array) {
            if (StringUtils.isNotBlank(value)) {
                isEmpty = false;
            }
            list.add(value);
        }
        if (isEmpty) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    public static boolean isIgnored(String path, OpenAPIConfiguration config) {
        if (config.getIgnoredRoutes() == null) {
            return false;
        }
        for (String item : config.getIgnoredRoutes()) {
            final int length = item.length();
            if (path.startsWith(item) && (path.length() == length || path.startsWith(PATH_DELIMITER, length))) {
                return true;
            }
        }
        return false;
    }

    public static HttpMethod[] extractOperationMethods(Method method, Iterator<OpenAPIExtension> chain, ClassLoader classLoader) {
        RouterAnnotationInfo router = RouterAnnotationInfoParser.getRouterAnnotation(method, classLoader);
        if (router != null) {
            var httpMethods = router.method();
            for (HttpMethod httpMethod : httpMethods) {
                if (httpMethod == HttpMethod.ALL) {
                    return new HttpMethod[] {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE,
                            HttpMethod.OPTIONS, HttpMethod.PATCH, HttpMethod.TRACE, HttpMethod.HEAD};
                }
            }
            return httpMethods;
        }
        var httpMethod = getHttpMethodsFromCustomAnnotations(method);
        if (httpMethod != null) {
            return httpMethod;
        } else if ((ReflectionUtils.getOverriddenMethod(method)) != null) {
            return extractOperationMethods(ReflectionUtils.getOverriddenMethod(method), chain, classLoader);
        } else if (chain != null && chain.hasNext()) {
            return chain.next().extractOperationMethods(method, chain);
        } else {
            return null;
        }
    }

    public static HttpMethod[] getHttpMethodsFromCustomAnnotations(Method method) {
        for (Annotation methodAnnotation : method.getAnnotations()) {
            Router router = methodAnnotation.annotationType().getAnnotation(Router.class);
            if (router != null) {
                var httpMethods = router.method();
                for (HttpMethod httpMethod : httpMethods) {
                    if (httpMethod == HttpMethod.ALL) {
                        return new HttpMethod[] {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE,
                                HttpMethod.OPTIONS, HttpMethod.PATCH, HttpMethod.TRACE, HttpMethod.HEAD};
                    }
                }
                return httpMethods;
            }
        }
        return null;
    }
}