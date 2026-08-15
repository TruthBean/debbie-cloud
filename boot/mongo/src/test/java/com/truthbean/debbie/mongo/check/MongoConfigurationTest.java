/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mongo.check;

import com.truthbean.debbie.mongo.MongoConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MongoConfigurationTest {

    @Test
    public void configurationDefaults() {
        var config = new MongoConfiguration();
        assertTrue(config.isEnable());
        assertEquals("localhost", config.getHost());
        assertEquals(27017, config.getPort());
        assertEquals(10000, config.getConnectTimeout());
        assertEquals(30000, config.getServerSelectionTimeout());
        assertEquals(100, config.getMaxPoolSize());
        assertEquals(0, config.getMinPoolSize());
        assertTrue(config.isRetryWrites());
        assertTrue(config.isRetryReads());
        assertFalse(config.isSsl());
    }

    @Test
    public void configurationShouldBuildConnectionString() {
        var config = new MongoConfiguration();
        config.setHost("mongo");
        config.setPort(27018);
        config.setDatabase("testdb");
        var connStr = config.buildConnectionString();
        assertTrue(connStr.startsWith("mongodb://mongo:27018/testdb"));
        assertTrue(connStr.contains("maxPoolSize=100"));
    }

    @Test
    public void configurationShouldUseUriWhenProvided() {
        var config = new MongoConfiguration();
        config.setUri("mongodb+srv://cluster.mongodb.net/mydb");
        assertEquals("mongodb+srv://cluster.mongodb.net/mydb", config.buildConnectionString());
    }

    @Test
    public void configurationShouldIncludeCredentials() {
        var config = new MongoConfiguration();
        config.setUsername("root");
        config.setPassword("secret");
        config.setAuthSource("admin");
        var connStr = config.buildConnectionString();
        assertTrue(connStr.contains("root:secret@"));
        assertTrue(connStr.contains("authSource=admin"));
    }

    @Test
    public void configurationShouldIncludeSslParams() {
        var config = new MongoConfiguration();
        config.setSsl(true);
        var connStr = config.buildConnectionString();
        assertTrue(connStr.contains("tls=true"));
    }

    @Test
    public void configurationShouldIncludeSslInvalidHost() {
        var config = new MongoConfiguration();
        config.setSsl(true);
        config.setSslInvalidHostAllowed(true);
        var connStr = config.buildConnectionString();
        assertTrue(connStr.contains("tlsAllowInvalidHostnames=true"));
    }

    @Test
    public void configurationShouldIncludePoolParams() {
        var config = new MongoConfiguration();
        config.setMaxPoolSize(50);
        config.setMinPoolSize(5);
        config.setMaxIdleTime(30000);
        config.setMaxWaitTime(60000);
        var connStr = config.buildConnectionString();
        assertTrue(connStr.contains("maxPoolSize=50"));
        assertTrue(connStr.contains("minPoolSize=5"));
        assertTrue(connStr.contains("maxIdleTimeMS=30000"));
        assertTrue(connStr.contains("waitQueueTimeoutMS=60000"));
    }

    @Test
    public void configurationShouldIncludeTimeoutParams() {
        var config = new MongoConfiguration();
        config.setConnectTimeout(5000);
        config.setSocketTimeout(3000);
        config.setServerSelectionTimeout(10000);
        var connStr = config.buildConnectionString();
        assertTrue(connStr.contains("connectTimeoutMS=5000"));
        assertTrue(connStr.contains("socketTimeoutMS=3000"));
        assertTrue(connStr.contains("serverSelectionTimeoutMS=10000"));
    }

    @Test
    public void configurationShouldDetectCredentials() {
        var config = new MongoConfiguration();
        assertFalse(config.hasCredentials());
        config.setUsername("user");
        config.setPassword("pass");
        assertTrue(config.hasCredentials());
    }

    @Test
    public void configurationShouldDetectUri() {
        var config = new MongoConfiguration();
        assertFalse(config.hasUri());
        config.setUri("mongodb://localhost");
        assertTrue(config.hasUri());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new MongoConfiguration();
        config.setHost("mongo-host");
        config.setPort(27018);
        config.setDatabase("mydb");
        config.setMaxPoolSize(20);

        var copy = config.<MongoConfiguration>copy();
        assertEquals("mongo-host", copy.getHost());
        assertEquals(27018, copy.getPort());
        assertEquals("mydb", copy.getDatabase());
        assertEquals(20, copy.getMaxPoolSize());
    }

    @Test
    public void configurationToStringShouldContainInfo() {
        var config = new MongoConfiguration();
        config.setHost("mongo");
        config.setPort(27017);
        var str = config.toString();
        assertTrue(str.contains("mongo"));
        assertTrue(str.contains("27017"));
    }

    @Test
    public void configurationShouldGetAndSetAllFields() {
        var config = new MongoConfiguration();
        config.setEnable(false);
        config.setUri("mongodb://localhost");
        config.setHost("mongo");
        config.setPort(27018);
        config.setDatabase("testdb");
        config.setUsername("user");
        config.setPassword("pass");
        config.setAuthSource("admin");
        config.setConnectTimeout(5000);
        config.setSocketTimeout(3000);
        config.setServerSelectionTimeout(10000);
        config.setMaxPoolSize(50);
        config.setMinPoolSize(5);
        config.setMaxIdleTime(30000);
        config.setMaxWaitTime(60000);
        config.setSsl(true);
        config.setSslInvalidHostAllowed(true);
        config.setRetryWrites(false);
        config.setRetryReads(false);

        assertFalse(config.isEnable());
        assertEquals("mongodb://localhost", config.getUri());
        assertEquals("mongo", config.getHost());
        assertEquals(27018, config.getPort());
        assertEquals("testdb", config.getDatabase());
        assertEquals("user", config.getUsername());
        assertEquals("pass", config.getPassword());
        assertEquals("admin", config.getAuthSource());
        assertEquals(5000, config.getConnectTimeout());
        assertEquals(3000, config.getSocketTimeout());
        assertEquals(10000, config.getServerSelectionTimeout());
        assertEquals(50, config.getMaxPoolSize());
        assertEquals(5, config.getMinPoolSize());
        assertEquals(30000, config.getMaxIdleTime());
        assertEquals(60000, config.getMaxWaitTime());
        assertTrue(config.isSsl());
        assertTrue(config.isSslInvalidHostAllowed());
        assertFalse(config.isRetryWrites());
        assertFalse(config.isRetryReads());
    }
}