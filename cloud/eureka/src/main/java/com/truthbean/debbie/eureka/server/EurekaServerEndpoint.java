/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka.server;

import com.truthbean.debbie.eureka.EurekaJson;
import com.truthbean.debbie.eureka.model.ApplicationInfo;
import com.truthbean.debbie.eureka.model.InstanceInfo;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.router.DeleteRouter;
import com.truthbean.debbie.mvc.router.GetRouter;
import com.truthbean.debbie.mvc.router.PostRouter;
import com.truthbean.debbie.mvc.router.PutRouter;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.mvc.response.provider.JsonResponseHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP endpoint that exposes the Eureka server REST API.
 * <p>
 * Routes (prefix configurable):
 * <ul>
 *   <li>GET    /{prefix}/apps — list all applications</li>
 *   <li>GET    /{prefix}/apps/{appName} — get application instances</li>
 *   <li>POST   /{prefix}/apps/{appName} — register an instance</li>
 *   <li>PUT    /{prefix}/apps/{appName}/{instanceId} — heartbeat</li>
 *   <li>DELETE /{prefix}/apps/{appName}/{instanceId} — deregister</li>
 *   <li>PUT    /{prefix}/apps/{appName}/{instanceId}/status — update status</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@Router
public class EurekaServerEndpoint {

    private final EurekaServerRegistry registry;
    private final String prefix;

    public EurekaServerEndpoint(EurekaServerRegistry registry, String prefix) {
        this.registry = registry;
        this.prefix = prefix;
    }

    @GetRouter(value = "/apps",
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Map<String, Object> listApps() {
        var result = new LinkedHashMap<String, Object>();
        var apps = new ArrayList<Map<String, Object>>();
        for (var app : registry.getAllApplications()) {
            apps.add(app.toMap());
        }
        result.put("applications", apps);
        return result;
    }

    @GetRouter(value = "/apps/{appName}",
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Object getApp(String appName) {
        var app = registry.getApplication(appName);
        if (app == null) {
            return errorResponse(404, "application " + appName + " not found");
        }
        return app.toMap();
    }

    @PostRouter(value = "/apps/{appName}",
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Map<String, Object> register(String appName, String body) {
        try {
            var parsed = EurekaJson.parseObject(body);
            var instance = InstanceInfo.fromMap(parsed);
            instance.setAppName(appName);
            registry.register(instance);
            return okResponse("registered " + instance.getInstanceId() + " to " + appName);
        } catch (Exception e) {
            return errorResponse(400, "failed to register: " + e.getMessage());
        }
    }

    @PutRouter(value = "/apps/{appName}/{instanceId}",
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Map<String, Object> heartbeat(String appName, String instanceId) {
        if (registry.renew(appName, instanceId)) {
            return okResponse("renewed " + appName + "/" + instanceId);
        }
        return errorResponse(404, "instance " + appName + "/" + instanceId + " not found");
    }

    @DeleteRouter(value = "/apps/{appName}/{instanceId}",
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Map<String, Object> deregister(String appName, String instanceId) {
        if (registry.cancel(appName, instanceId)) {
            return okResponse("deregistered " + appName + "/" + instanceId);
        }
        return errorResponse(404, "instance " + appName + "/" + instanceId + " not found");
    }

    @PutRouter(value = "/apps/{appName}/{instanceId}/status",
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Map<String, Object> updateStatus(String appName, String instanceId, String value) {
        if (registry.updateStatus(appName, instanceId, value)) {
            return okResponse("status updated to " + value);
        }
        return errorResponse(404, "instance " + appName + "/" + instanceId + " not found");
    }

    private Map<String, Object> okResponse(String message) {
        var m = new LinkedHashMap<String, Object>();
        m.put("status", "OK");
        m.put("message", message);
        return m;
    }

    private Map<String, Object> errorResponse(int code, String message) {
        var m = new LinkedHashMap<String, Object>();
        m.put("status", "ERROR");
        m.put("code", code);
        m.put("message", message);
        return m;
    }

    public String getPrefix() { return prefix; }
}