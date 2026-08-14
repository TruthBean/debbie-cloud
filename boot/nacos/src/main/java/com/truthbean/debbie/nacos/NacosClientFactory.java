/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class NacosClientFactory {

    private final NacosConfiguration configuration;
    private volatile ConfigService configService;
    private volatile NamingService namingService;

    public NacosClientFactory(NacosConfiguration configuration) {
        this.configuration = configuration;
    }

    private Properties createProperties() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, configuration.getServerAddr());
        if (configuration.getNamespace() != null && !configuration.getNamespace().isEmpty()) {
            properties.setProperty(PropertyKeyConst.NAMESPACE, configuration.getNamespace());
        }
        if (configuration.getUsername() != null && !configuration.getUsername().isEmpty()) {
            properties.setProperty(PropertyKeyConst.USERNAME, configuration.getUsername());
        }
        if (configuration.getPassword() != null && !configuration.getPassword().isEmpty()) {
            properties.setProperty(PropertyKeyConst.PASSWORD, configuration.getPassword());
        }
        return properties;
    }

    /**
     * Get or create the ConfigService.
     */
    public ConfigService getConfigService() throws NacosException {
        if (configService == null) {
            synchronized (this) {
                if (configService == null) {
                    configService = NacosFactory.createConfigService(createProperties());
                    LOGGER.info("Nacos ConfigService created");
                }
            }
        }
        return configService;
    }

    /**
     * Get or create the NamingService.
     */
    public NamingService getNamingService() throws NacosException {
        if (namingService == null) {
            synchronized (this) {
                if (namingService == null) {
                    namingService = NacosFactory.createNamingService(createProperties());
                    LOGGER.info("Nacos NamingService created");
                }
            }
        }
        return namingService;
    }

    public NacosConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        try {
            if (configService != null) {
                configService.shutDown();
                LOGGER.info("Nacos ConfigService shutdown");
            }
        } catch (NacosException e) {
            LOGGER.error("Failed to shutdown Nacos ConfigService", e);
        }
        try {
            if (namingService != null) {
                namingService.shutDown();
                LOGGER.info("Nacos NamingService shutdown");
            }
        } catch (NacosException e) {
            LOGGER.error("Failed to shutdown Nacos NamingService", e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosClientFactory.class);
}