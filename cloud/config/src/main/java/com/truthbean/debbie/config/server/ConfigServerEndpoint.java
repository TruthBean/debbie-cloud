/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config.server;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.config.ConfigConfiguration;
import com.truthbean.debbie.config.encrypt.ConfigEncryptor;
import com.truthbean.debbie.config.env.ConfigEnvironment;
import com.truthbean.debbie.config.env.ConfigPropertySource;
import com.truthbean.debbie.config.repository.ConfigRepository;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.router.GetRouter;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.mvc.response.provider.JsonResponseHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * config server http endpoint.
 * <p>
 * exposes the following routes (prefix configurable via {@code debbie.config.server.prefix}):
 * <ul>
 *   <li>{@code GET {prefix}/{application}/{profile}} - get environment</li>
 *   <li>{@code GET {prefix}/{application}/{profile}/{label}} - get environment with label</li>
 *   <li>{@code GET {prefix}/{application}-{profile}.properties} - get flattened properties</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@Router
public class ConfigServerEndpoint {

    private final ConfigConfiguration configuration;
    private final ConfigRepository repository;
    private final ConfigEncryptor encryptor;

    public ConfigServerEndpoint(ConfigConfiguration configuration,
                                ConfigRepository repository,
                                ConfigEncryptor encryptor) {
        this.configuration = configuration;
        this.repository = repository;
        this.encryptor = encryptor;
    }

    @GetRouter(value = {"/{application}/{profile}"},
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public ConfigEnvironment getEnvironment(String application, String profile) {
        return getEnvironmentWithLabel(application, profile, null);
    }

    @GetRouter(value = {"/{application}/{profile}/{label}"},
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public ConfigEnvironment getEnvironmentWithLabel(String application, String profile, String label) {
        LOGGER.debug(() -> "config server: " + application + "/" + profile + "/" + label);
        var env = repository.findOne(application, profile, label);
        if (encryptor != null) {
            decryptEnvironment(env);
        }
        return env;
    }

    @GetRouter(value = {"/{application}-{profile}.properties"},
            responseType = MediaType.TEXT_PLAIN_UTF8)
    public String getProperties(String application, String profile) {
        var env = repository.findOne(application, profile, null);
        if (encryptor != null) {
            decryptEnvironment(env);
        }
        var map = env.asFlattenedMap();
        var sb = new StringBuilder();
        for (var entry : map.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
        }
        return sb.toString();
    }

    private void decryptEnvironment(ConfigEnvironment env) {
        for (ConfigPropertySource ps : env.getPropertySources()) {
            var decrypted = new LinkedHashMap<String, String>();
            for (var entry : ps.getSource().entrySet()) {
                decrypted.put(entry.getKey(), encryptor.decryptIfEncrypted(entry.getValue()));
            }
            ps.getSource().clear();
            ps.putAll(decrypted);
        }
    }

    public String getPrefix() {
        return configuration.getServerPrefix();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServerEndpoint.class);
}