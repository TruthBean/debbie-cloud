/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
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
    requires transitive com.truthbean.debbie.servlet;
    requires transitive com.truthbean.debbie.server;
    requires transitive com.truthbean.logger.juli;
    requires org.apache.tomcat.juli;

    requires transitive org.apache.tomcat.catalina;
    // requires transitive org.apache.tomcat.embed.core;
    // requires transitive org.apache.tomcat.embed.jasper;
    // requires transitive org.apache.tomcat.embed.el;
    requires java.management;

    requires jakarta.servlet;
    requires jakarta.annotation;
    requires org.apache.tomcat.api;
    requires org.apache.tomcat.jni;
    requires org.apache.tomcat.coyote;
    requires org.apache.tomcat.util;
    requires org.apache.tomcat.util.scan;
    requires jakarta.security.auth.message;
    requires org.apache.tomcat.jasper;
    requires jakarta.el;

    requires ecj;

    requires org.apache.tomcat.jasper.el;

    provides com.truthbean.debbie.boot.AbstractApplication with
            com.truthbean.debbie.tomcat.TomcatServerApplication;

    provides com.truthbean.debbie.boot.DebbieModuleStarter with com.truthbean.debbie.tomcat.TomcatModuleStarter;
}