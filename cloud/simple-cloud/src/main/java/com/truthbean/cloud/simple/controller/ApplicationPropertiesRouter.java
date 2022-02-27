/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.cloud.simple.controller;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.response.provider.PropertiesResponseHandler;
import com.truthbean.debbie.mvc.router.GetRouter;
import com.truthbean.debbie.mvc.router.PostRouter;
import com.truthbean.debbie.mvc.router.Router;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-19 18:22
 */
@Router
public class ApplicationPropertiesRouter implements Closeable {

    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public ApplicationPropertiesRouter() {
        cache.put("debbie.application.name", "debbie-cms");
        cache.put("debbie.web.default.response.types", "application/json;Charset=UTF-8");
        cache.put("debbie.web.default.content.accept-client", "true");
        cache.put("debbie.web.dispatcher-mapping", "**.do");
    }

    @GetRouter(value = "/ping", responseType = MediaType.TEXT_PLAIN_UTF8)
    public String ping() {
        return "pong";
    }

    @PostRouter(value = "/application.properties", hasTemplate = true, responseType = MediaType.TEXT_PLAIN_UTF8,
            handlerClass = PropertiesResponseHandler.class)
    public Map<String, String> addApplicationProperty(String name, String value) {
        cache.put(name, value);
        return cache;
    }

    @GetRouter(value = "/application.properties", hasTemplate = true, responseType = MediaType.TEXT_PLAIN_UTF8,
            handlerClass = PropertiesResponseHandler.class)
    public Map<String, String> applicationProperties() {
        return cache;
    }

    @Override
    public void close() throws IOException {
        cache.clear();
    }

}
