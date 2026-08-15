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
 * spi factory to create a {@link ConfigRepository}.
 * <p>
 * implementations are discovered via {@link java.util.ServiceLoader}
 * under {@code META-INF/services/com.truthbean.debbie.config.repository.ConfigRepositoryFactory}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface ConfigRepositoryFactory {

    String name();

    boolean support(ConfigConfiguration configuration);

    ConfigRepository create(ConfigConfiguration configuration);
}