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

/**
 * encryptor / decryptor for configuration values.
 * <p>
 * values prefixed with {@code {cipher}} are decrypted by the config server
 * before being sent to clients (or by the client if server-side decryption
 * is disabled).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface ConfigEncryptor {

    String CIPHER_PREFIX = "{cipher}";

    String name();

    String encrypt(String plain);

    String decrypt(String cipher);

    default boolean isEncrypted(String value) {
        return value != null && value.startsWith(CIPHER_PREFIX);
    }

    default String decryptIfEncrypted(String value) {
        if (isEncrypted(value)) {
            return decrypt(value.substring(CIPHER_PREFIX.length()));
        }
        return value;
    }
}