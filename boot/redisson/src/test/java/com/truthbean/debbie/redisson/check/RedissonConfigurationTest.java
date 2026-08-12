package com.truthbean.debbie.redisson.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.redisson.RedissonClientFactory;
import com.truthbean.debbie.redisson.RedissonConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class RedissonConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject RedissonConfiguration configuration) {
        Console.println(configuration);
        Console.println("host: " + configuration.getHost());
        Console.println("port: " + configuration.getPort());
        Console.println("database: " + configuration.getDatabase());
        Console.println("connectTimeout: " + configuration.getConnectTimeout());
        Console.println("timeout: " + configuration.getTimeout());
        Console.println("retryAttempts: " + configuration.getRetryAttempts());
        Console.println("retryInterval: " + configuration.getRetryInterval());
        Console.println("connectionPoolSize: " + configuration.getConnectionPoolSize());
        Console.println("connectionMinIdleSize: " + configuration.getConnectionMinIdleSize());
        Console.println("idleConnectionTimeout: " + configuration.getIdleConnectionTimeout());
        Console.println("pingConnectionInterval: " + configuration.getPingConnectionInterval());
        Console.println("keepAlive: " + configuration.isKeepAlive());
        Console.println("dnsMonitoringInterval: " + configuration.getDnsMonitoringInterval());
    }

    @Test
    public void testClientFactory(@BeanInject RedissonClientFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
    }
}