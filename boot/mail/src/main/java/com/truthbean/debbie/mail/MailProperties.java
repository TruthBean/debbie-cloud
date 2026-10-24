/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mail;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-mail.
 * <p>
 * Reads the {@link MailConfiguration} from the environment using the prefix
 * {@code debbie.mail.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MailProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<MailConfiguration> {

    /**
     * key prefix of the mail properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.mail.host}
     */
    private static final String KEY_PREFIX = "debbie.mail.";

    private final Map<String, MailConfiguration> map = new HashMap<>();
    private MailConfiguration configuration;

    public MailProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, MailConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public MailConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public MailConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new MailConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 25));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setPassword(getValue(KEY_PREFIX + "password"));
        configuration.setProtocol(getStringValue(KEY_PREFIX + "protocol", "smtp"));
        configuration.setDefaultEncoding(getStringValue(KEY_PREFIX + "default-encoding", "UTF-8"));
        configuration.setSsl(getBooleanValue(KEY_PREFIX + "ssl", false));
        configuration.setStarttls(getBooleanValue(KEY_PREFIX + "starttls", false));
        configuration.setAuth(getBooleanValue(KEY_PREFIX + "auth", true));
        configuration.setConnectionTimeout(getIntegerValue(KEY_PREFIX + "connection-timeout", 5000));
        configuration.setTimeout(getIntegerValue(KEY_PREFIX + "timeout", 5000));
        configuration.setWriteTimeout(getIntegerValue(KEY_PREFIX + "write-timeout", 5000));
        configuration.setDefaultFrom(getValue(KEY_PREFIX + "default-from"));
        configuration.setDefaultFromPersonal(getValue(KEY_PREFIX + "default-from-personal"));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}