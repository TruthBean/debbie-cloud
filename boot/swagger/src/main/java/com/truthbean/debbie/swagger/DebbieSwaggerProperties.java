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

import com.truthbean.debbie.env.EnvironmentContentHolder;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieSwaggerProperties extends EnvironmentContentHolder {
    public static final String ENABLE_KEY = "debbie.swagger.enable";
    //===================================================================================================
    private static final String INFO_TITLE = "debbie.swagger.info.title";
    private static final String INFO_DESCRIPTION = "debbie.swagger.info.description";
    private static final String INFO_TERMS_OF_SERVICE = "debbie.swagger.info.terms-of-service";
    private static final String INFO_VERSION = "debbie.swagger.info.version";

    private static final String INFO_CONTACT_NAME = "debbie.swagger.info.contact.name";
    private static final String INFO_CONTACT_EMAIL = "debbie.swagger.info.contact.email";
    private static final String INFO_CONTACT_URL = "debbie.swagger.info.contact.url";

    private static final String INFO_LICENSE_URL = "debbie.swagger.info.license.url";
    private static final String INFO_LICENSE_NAME = "debbie.swagger.info.license.name";

    private static final String SERVER_URL = "debbie.swagger.server.url";
    private static final String SERVER_DESCRIPTION = "debbie.swagger.server.description";
    //===================================================================================================

    private final Info info;
    private final Server server;

    public DebbieSwaggerProperties() {
        info = new Info()
                .title(getValue(INFO_TITLE))
                .description(getValue(INFO_DESCRIPTION))
                .termsOfService(getValue(INFO_TERMS_OF_SERVICE))
                .contact(new Contact()
                        .name(getValue(INFO_CONTACT_NAME))
                        .email(getValue(INFO_CONTACT_EMAIL))
                        .url(getValue(INFO_CONTACT_URL))
                )
                .license(new License()
                        .name(getValue(INFO_LICENSE_NAME))
                        .url(getValue(INFO_LICENSE_URL))
                )
                .version(getValue(INFO_VERSION));

        server = new Server().url(getValue(SERVER_URL)).description(getValue(SERVER_DESCRIPTION));
    }

    public Info getInfo() {
        return info;
    }

    public Server getServer() {
        return server;
    }

    public boolean isEnable() {
        return getBooleanValue(ENABLE_KEY, false);
    }
}
