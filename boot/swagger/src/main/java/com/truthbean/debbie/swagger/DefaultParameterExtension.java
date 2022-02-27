/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
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

import com.truthbean.debbie.mvc.request.RequestParameterInfo;
import com.truthbean.common.mini.util.StringUtils;
import io.swagger.v3.core.util.ParameterProcessor;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DefaultParameterExtension extends AbstractOpenAPIExtension {
    private static final String QUERY_PARAM = "query";
    private static final String HEADER_PARAM = "header";
    private static final String COOKIE_PARAM = "cookie";
    private static final String PATH_PARAM = "path";
    private static final String FORM_PARAM = "form";

    private final ClassLoader classLoader;
    public DefaultParameterExtension(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ResolvedParameter extractParameters(List<Annotation> annotations,
                                               Type type,
                                               Set<Type> typesToSkip,
                                               Components components,
                                               MediaTypeInfo consumes,
                                               boolean includeRequestBody,
                                               JsonView jsonViewAnnotation,
                                               Iterator<OpenAPIExtension> chain) {
        if (shouldIgnoreType(type, typesToSkip)) {
            return new ResolvedParameter();
        }


        Parameter parameter = null;
        for (Annotation annotation : annotations) {
            RequestParameterInfo param = RequestParameterInfo.fromAnnotation(annotation, classLoader);
            if (param != null) {
                String name;
                switch (param.paramType()) {
                    case QUERY:
                        parameter = new Parameter();
                        parameter.setIn(QUERY_PARAM);
                        name = param.value();
                        if (name.isBlank()) {
                            name = param.name();
                        }
                        parameter.setName(name);
                        break;
                    case PATH:
                        parameter = new Parameter();
                        parameter.setIn(PATH_PARAM);
                        name = param.value();
                        if (name.isBlank()) {
                            name = param.name();
                        }
                        parameter.setName(name);
                        break;
                    case MATRIX:
                        parameter = new Parameter();
                        parameter.setIn(PATH_PARAM);
                        parameter.setStyle(Parameter.StyleEnum.MATRIX);
                        name = param.value();
                        if (name.isBlank()) {
                            name = param.name();
                        }
                        parameter.setName(name);
                        break;
                    case HEAD:
                        parameter = new Parameter();
                        parameter.setIn(HEADER_PARAM);
                        name = param.value();
                        if (name.isBlank()) {
                            name = param.name();
                        }
                        parameter.setName(name);
                        break;
                    case COOKIE:
                        parameter = new Parameter();
                        parameter.setIn(COOKIE_PARAM);
                        name = param.value();
                        if (name.isBlank()) {
                            name = param.name();
                        }
                        parameter.setName(name);
                        break;
                    case PARAM:
                        parameter = new Parameter();
                        parameter.setIn(FORM_PARAM);
                        name = param.value();
                        if (name.isBlank()) {
                            name = param.name();
                        }
                        parameter.setName(name);
                        break;
                    default:
                        break;
                }
            } else if (annotation instanceof io.swagger.v3.oas.annotations.Parameter) {
                if (((io.swagger.v3.oas.annotations.Parameter) annotation).hidden()) {
                    return new ResolvedParameter();
                }
                if (parameter == null) {
                    parameter = new Parameter();
                }
                if (StringUtils.isNotBlank(((io.swagger.v3.oas.annotations.Parameter) annotation).ref())) {
                    parameter.$ref(((io.swagger.v3.oas.annotations.Parameter) annotation).ref());
                }
            }
        }
        List<Parameter> parameters = new ArrayList<>();
        ResolvedParameter extractParametersResult = new ResolvedParameter();

        if (parameter != null && (StringUtils.isNotBlank(parameter.getIn()) || StringUtils.isNotBlank(parameter.get$ref()))) {
            parameters.add(parameter);
        } else if (includeRequestBody) {
            Parameter unknownParameter = ParameterProcessor.applyAnnotations(
                    null,
                    type,
                    annotations,
                    components,
                    new String[0],
                    consumes == null ? new String[0] : new String[]{consumes.toString()}, jsonViewAnnotation);
            if (unknownParameter != null) {
                if (StringUtils.isNotBlank(unknownParameter.getIn()) && !"form".equals(unknownParameter.getIn())) {
                    extractParametersResult.addParameter(unknownParameter);
                } else if ("form".equals(unknownParameter.getIn())) {
                    unknownParameter.setIn(null);
                    extractParametersResult.addFormParameter(unknownParameter);
                } else {
                    // return as request body
                    extractParametersResult.setRequestBody(unknownParameter);
                }
            }
        }
        for (Parameter p : parameters) {
            Parameter processedParameter = ParameterProcessor.applyAnnotations(
                    p,
                    type,
                    annotations,
                    components,
                    new String[0],
                    consumes == null ? new String[0] : new String[]{consumes.toString()},
                    jsonViewAnnotation);
            if (processedParameter != null) {
                extractParametersResult.addParameter(processedParameter);
            }
        }
        return extractParametersResult;
    }

    @Override
    protected boolean shouldIgnoreClass(Class<?> cls) {
        return cls.getName().startsWith("com.truthbean.debbie.mvc");
    }

}