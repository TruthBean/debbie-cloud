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
open module com.truthbean.debbie.servlet.test {
    requires transitive com.truthbean.debbie.servlet;
    requires com.truthbean.debbie.test;

    requires transitive com.truthbean.logger.juli;
    requires transitive org.apache.tomcat.juli;

    requires transitive org.apache.tomcat.catalina;
    requires static java.management;

    requires transitive jakarta.servlet;
    requires transitive jakarta.annotation;
    requires transitive org.apache.tomcat.api;
    requires transitive org.apache.tomcat.jni;
    requires transitive org.apache.tomcat.coyote;
    requires transitive org.apache.tomcat.util;
    requires transitive org.apache.tomcat.util.scan;
    requires transitive jakarta.security.auth.message;
    requires transitive jakarta.servlet.jsp.jstl;
    requires transitive org.apache.tomcat.jasper;
    requires transitive jakarta.el;
}