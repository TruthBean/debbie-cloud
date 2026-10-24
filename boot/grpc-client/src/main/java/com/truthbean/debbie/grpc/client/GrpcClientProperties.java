/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.grpc.client;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-grpc-client.
 * <p>
 * Reads the {@link GrpcClientConfiguration} from the environment using the prefix
 * {@code debbie.grpc.client.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GrpcClientProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<GrpcClientConfiguration> {

    /**
     * key prefix of the grpc client properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.grpc.client.port}
     */
    private static final String KEY_PREFIX = "debbie.grpc.client.";

    private final Map<String, GrpcClientConfiguration> map = new HashMap<>();
    private GrpcClientConfiguration configuration;

    public GrpcClientProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, GrpcClientConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public GrpcClientConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public GrpcClientConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new GrpcClientConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 9090));
        configuration.setUsePlaintext(getBooleanValue(KEY_PREFIX + "use-plaintext", true));
        configuration.setMaxInboundMessageSize(getIntegerValue(KEY_PREFIX + "max-inbound-message-size", 4194304));
        configuration.setKeepAliveTime(getIntegerValue(KEY_PREFIX + "keep-alive-time", 300));
        configuration.setKeepAliveTimeout(getIntegerValue(KEY_PREFIX + "keep-alive-timeout", 20));
        configuration.setKeepAliveWithoutCalls(getBooleanValue(KEY_PREFIX + "keep-alive-without-calls", true));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}