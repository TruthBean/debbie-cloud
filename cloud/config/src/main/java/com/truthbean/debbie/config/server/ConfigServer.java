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

import com.truthbean.debbie.config.ConfigConfiguration;
import com.truthbean.debbie.config.encrypt.ConfigEncryptor;
import com.truthbean.debbie.config.repository.ConfigRepository;

/**
 * holds the assembled config server components.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConfigServer {

    private final ConfigConfiguration configuration;
    private final ConfigRepository repository;
    private final ConfigEncryptor encryptor;
    private final ConfigServerEndpoint endpoint;

    public ConfigServer(ConfigConfiguration configuration,
                        ConfigRepository repository,
                        ConfigEncryptor encryptor) {
        this.configuration = configuration;
        this.repository = repository;
        this.encryptor = encryptor;
        this.endpoint = new ConfigServerEndpoint(configuration, repository, encryptor);
    }

    public ConfigConfiguration getConfiguration() {
        return configuration;
    }

    public ConfigRepository getRepository() {
        return repository;
    }

    public ConfigEncryptor getEncryptor() {
        return encryptor;
    }

    public ConfigServerEndpoint getEndpoint() {
        return endpoint;
    }
}