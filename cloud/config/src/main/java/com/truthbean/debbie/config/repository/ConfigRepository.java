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

import com.truthbean.debbie.config.env.ConfigEnvironment;

/**
 * abstraction of a configuration repository backend.
 * <p>
 * concrete implementations may read from file system, git, jdbc, vault, etc.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface ConfigRepository {

    String name();

    /**
     * find one {@link ConfigEnvironment} by application name, profiles and label.
     *
     * @param application the application name
     * @param profile     comma separated profiles (e.g. "dev,cloud")
     * @param label       the label (e.g. git branch), may be null
     */
    ConfigEnvironment findOne(String application, String profile, String label);
}