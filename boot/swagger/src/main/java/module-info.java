/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-04 20:15
 */
module com.truthbean.debbie.swagger {
    exports com.truthbean.debbie.swagger;
    uses com.truthbean.debbie.swagger.OpenAPIExtension;

    opens com.truthbean.debbie.swagger to com.truthbean.common.mini, com.truthbean.debbie.core;

    requires transitive com.truthbean.debbie.mvc;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.dataformat.yaml;
    requires static io.swagger.v3.oas.models;
    requires static io.swagger.v3.oas.integration;
    requires static io.swagger.v3.core;
    requires static io.swagger.v3.oas.annotations;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires transitive io.github.classgraph;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.swagger.SwaggerModuleStarter;
}