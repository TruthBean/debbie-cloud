/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
module com.truthbean.debbie.tomcat {
    requires transitive com.truthbean.debbie.server;
    requires transitive org.apache.tomcat.embed.core;
    requires transitive org.apache.tomcat.embed.jasper;
    requires transitive org.apache.tomcat.embed.el;
    requires static java.management;

    requires transitive jakarta.annotation;

    requires transitive ecj;
    requires transitive jakarta.servlet.jsp.jstl;

    opens com.truthbean.logger.juli;
    exports com.truthbean.logger.juli;

    exports com.truthbean.debbie.servlet;
    exports com.truthbean.debbie.servlet.response.view;

    provides org.apache.juli.logging.Log with com.truthbean.logger.juli.JuliLogger;

    provides com.truthbean.debbie.boot.AbstractApplication with com.truthbean.debbie.tomcat.TomcatServerApplication;

    provides com.truthbean.debbie.boot.DebbieModuleStarter with com.truthbean.debbie.tomcat.TomcatModuleStarter;

    provides com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler
            with com.truthbean.debbie.servlet.response.view.JspHandler;

    provides jakarta.servlet.ServletContainerInitializer
            with com.truthbean.debbie.servlet.ServletApplicationInitializer;
}