/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.grpc.server;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-grpc-server.
 * <p>
 * Reads the {@link GrpcServerConfiguration} from the environment using the prefix
 * {@code debbie.grpc.server.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GrpcServerProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<GrpcServerConfiguration> {

    /**
     * key prefix of the grpc server properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.grpc.server.port}
     */
    private static final String KEY_PREFIX = "debbie.grpc.server.";

    private final Map<String, GrpcServerConfiguration> map = new HashMap<>();
    private GrpcServerConfiguration configuration;

    public GrpcServerProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, GrpcServerConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return Map.of();
    }

    @Override
    public Set<String> getCategories(String profile) {
        return Set.of();
    }

    @Override
    public boolean containConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return false;
    }

    @Override
    public GrpcServerConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public GrpcServerConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new GrpcServerConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 9090));
        configuration.setMaxInboundMessageSize(getIntegerValue(KEY_PREFIX + "max-inbound-message-size", 4194304));
        configuration.setMaxInboundMetadataSize(getIntegerValue(KEY_PREFIX + "max-inbound-metadata-size", 8192));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}