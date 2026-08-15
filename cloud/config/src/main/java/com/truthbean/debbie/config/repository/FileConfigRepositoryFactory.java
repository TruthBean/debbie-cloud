/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config.repository;

import com.truthbean.debbie.config.ConfigConfiguration;

/**
 * spi factory for {@link FileConfigRepository}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FileConfigRepositoryFactory implements ConfigRepositoryFactory {

    @Override
    public String name() {
        return "file";
    }

    @Override
    public boolean support(ConfigConfiguration configuration) {
        return configuration != null && "file".equalsIgnoreCase(configuration.getServerRepository());
    }

    @Override
    public ConfigRepository create(ConfigConfiguration configuration) {
        return new FileConfigRepository(configuration);
    }
}