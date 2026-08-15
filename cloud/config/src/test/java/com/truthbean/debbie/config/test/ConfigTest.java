/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config.test;

import com.truthbean.debbie.config.ConfigConfiguration;
import com.truthbean.debbie.config.env.ConfigEnvironment;
import com.truthbean.debbie.config.env.ConfigPropertySource;
import com.truthbean.debbie.config.encrypt.ConfigEncryptor;
import com.truthbean.debbie.config.encrypt.NoopConfigEncryptor;
import com.truthbean.debbie.config.repository.ConfigRepository;
import com.truthbean.debbie.config.repository.ConfigRepositoryFactory;
import com.truthbean.debbie.config.repository.FileConfigRepository;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConfigTest {

    @Test
    public void spiShouldLoadFileRepositoryFactory() {
        var loader = ServiceLoader.load(ConfigRepositoryFactory.class);
        boolean found = false;
        for (var f : loader) {
            if ("file".equals(f.name())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "FileConfigRepositoryFactory should be loadable via spi");
    }

    @Test
    public void configEnvironmentShouldFlatten() {
        var env = new ConfigEnvironment("order", java.util.List.of("dev"), "master");
        env.addPropertySource(new ConfigPropertySource("app", java.util.Map.of("a", "1", "b", "2")));
        env.addPropertySource(new ConfigPropertySource("app-dev", java.util.Map.of("b", "20", "c", "3")));

        var flat = env.asFlattenedMap();
        assertEquals("1", flat.get("a"));
        assertEquals("20", flat.get("b"));
        assertEquals("3", flat.get("c"));
    }

    @Test
    public void configEnvironmentFirstWinsShouldKeepFirst() {
        var env = new ConfigEnvironment("order", java.util.List.of("dev"), "master");
        env.addPropertySource(new ConfigPropertySource("app", java.util.Map.of("a", "1", "b", "2")));
        env.addPropertySource(new ConfigPropertySource("app-dev", java.util.Map.of("b", "20", "c", "3")));

        var flat = env.asFlattenedMapFirstWins();
        assertEquals("1", flat.get("a"));
        assertEquals("2", flat.get("b"));
        assertEquals("3", flat.get("c"));
    }

    @Test
    public void noopEncryptorShouldReturnAsIs() {
        ConfigEncryptor enc = new NoopConfigEncryptor();
        assertEquals("hello", enc.encrypt("hello"));
        assertEquals("hello", enc.decrypt("hello"));
        assertFalse(enc.isEncrypted("hello"));
        assertTrue(enc.isEncrypted("{cipher}abc"));
        assertEquals("abc", enc.decryptIfEncrypted("{cipher}abc"));
        assertEquals("hello", enc.decryptIfEncrypted("hello"));
    }

    @Test
    public void fileRepositoryShouldHandleMissingDir() {
        var config = new ConfigConfiguration();
        config.setServerBaseDir("nonexistent-config-dir");
        config.setServerDefaultLabel("master");

        ConfigRepository repo = new FileConfigRepository(config);
        var env = repo.findOne("order", "dev", null);
        assertNotNull(env);
        assertEquals("order", env.getName());
        assertTrue(env.getPropertySources().isEmpty());
    }

    @Test
    public void configConfigurationDefaults() {
        var config = new ConfigConfiguration();
        assertTrue(config.isEnable());
        assertFalse(config.isServerEnable());
        assertFalse(config.isClientEnable());
        assertEquals("file", config.getServerRepository());
        assertEquals("master", config.getServerDefaultLabel());
        assertEquals("/config", config.getServerPrefix());
        assertEquals("http://localhost:8888/config", config.getClientUri());
        assertEquals("application", config.getClientName());
        assertEquals("default", config.getClientProfile());
    }
}