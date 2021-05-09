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
module com.truthbean.debbie.servlet {
    exports com.truthbean.debbie.servlet;
    exports com.truthbean.debbie.servlet.response.view;

    requires transitive com.truthbean.debbie.mvc;
    requires jakarta.servlet;
    requires static org.apache.commons.io;
    requires jakarta.servlet.jsp.jstl;
    requires jakarta.servlet.jsp;
    requires jakarta.el;
    requires jakarta.xml.bind;
    requires jakarta.activation;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.servlet.ServletModuleStarter;

    provides com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler
            with com.truthbean.debbie.servlet.response.view.JspHandler;

    provides jakarta.servlet.ServletContainerInitializer
            with com.truthbean.debbie.servlet.ServletApplicationInitializer;
}