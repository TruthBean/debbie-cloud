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

import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;

class ResolvedParameter {
    private final List<Parameter> parameters;
    private Parameter requestBody;
    private final List<Parameter> formParameters;

    ResolvedParameter() {
        parameters = new ArrayList<>();
        formParameters = new ArrayList<>();
    }

    public void setRequestBody(Parameter requestBody) {
        this.requestBody = requestBody;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addParameters(List<Parameter> parameters) {
        this.parameters.addAll(parameters);
    }

    public void addParameter(Parameter parameter) {
        this.parameters.add(parameter);
    }

    public Parameter getRequestBody() {
        return requestBody;
    }

    public List<Parameter> getFormParameters() {
        return formParameters;
    }

    public void addFormParameters(List<Parameter> formParameters) {
        this.formParameters.addAll(formParameters);
    }

    public void addFormParameter(Parameter formParameter) {
        this.formParameters.add(formParameter);
    }
}