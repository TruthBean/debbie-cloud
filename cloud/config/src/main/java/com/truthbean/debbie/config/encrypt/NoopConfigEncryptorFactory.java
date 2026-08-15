/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config.encrypt;

import com.truthbean.debbie.config.ConfigConfiguration;

/**
 * spi factory for {@link NoopConfigEncryptor}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class NoopConfigEncryptorFactory implements ConfigEncryptorFactory {

    @Override
    public String name() {
        return "noop";
    }

    @Override
    public boolean support(ConfigConfiguration configuration) {
        return configuration != null && !configuration.isServerEncrypt();
    }

    @Override
    public ConfigEncryptor create(ConfigConfiguration configuration) {
        return new NoopConfigEncryptor();
    }
}