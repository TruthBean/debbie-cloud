package com.truthbean.debbie.jedis.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jedis.JedisClientFactory;
import com.truthbean.debbie.jedis.JedisConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class JedisConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject JedisConfiguration configuration) {
        Console.println(configuration);
        Console.println("host: " + configuration.getHost());
        Console.println("port: " + configuration.getPort());
        Console.println("database: " + configuration.getDatabase());
        Console.println("timeout: " + configuration.getTimeout());
        Console.println("maxTotal: " + configuration.getMaxTotal());
        Console.println("maxIdle: " + configuration.getMaxIdle());
        Console.println("minIdle: " + configuration.getMinIdle());
        Console.println("maxWaitMillis: " + configuration.getMaxWaitMillis());
    }

    @Test
    public void testClientFactory(@BeanInject JedisClientFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
        Console.println(factory.getPool());
    }
}
