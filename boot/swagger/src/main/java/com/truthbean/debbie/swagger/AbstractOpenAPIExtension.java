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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.request.HttpMethod;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

abstract class AbstractOpenAPIExtension implements OpenAPIExtension {

    @Override
    public HttpMethod extractOperationMethod(Method method, Iterator<OpenAPIExtension> chain) {
        if (chain.hasNext()) {
            return chain.next().extractOperationMethod(method, chain);
        } else {
            return null;
        }
    }

    @Override
    public HttpMethod[] extractOperationMethods(Method method, Iterator<OpenAPIExtension> chain) {
        if (chain.hasNext()) {
            return chain.next().extractOperationMethods(method, chain);
        } else {
            return null;
        }
    }

    @Override
    public ResolvedParameter extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip,
                                               Components components, MediaTypeInfo consumes, boolean includeRequestBody,
                                               JsonView jsonViewAnnotation, Iterator<OpenAPIExtension> chain) {
        if (chain.hasNext()) {
            return chain.next().extractParameters(annotations, type, typesToSkip, components, consumes, includeRequestBody, jsonViewAnnotation, chain);
        } else {
            return new ResolvedParameter();
        }
    }

    @Override
    public void decorateOperation(Operation operation, Method method, Iterator<OpenAPIExtension> chain) {
        if (chain.hasNext()) {
            chain.next().decorateOperation(operation, method, chain);
        }
    }

    protected boolean shouldIgnoreClass(Class<?> cls) {
        return false;
    }

    protected boolean shouldIgnoreType(Type type, Set<Type> typesToSkip) {
        if (typesToSkip.contains(type)) {
            return true;
        }
        if (shouldIgnoreClass(constructType(type).getRawClass())) {
            typesToSkip.add(type);
            return true;
        }
        return false;
    }

    protected JavaType constructType(Type type) {
        return TypeFactory.defaultInstance().constructType(type);
    }
}