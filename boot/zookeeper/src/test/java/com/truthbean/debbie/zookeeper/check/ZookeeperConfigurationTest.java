package com.truthbean.debbie.zookeeper.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import com.truthbean.debbie.zookeeper.ZookeeperConfiguration;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class ZookeeperConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject ZookeeperConfiguration configuration) {
        Console.println(configuration);
        Console.println("connectionString: " + configuration.getConnectionString());
        Console.println("sessionTimeout: " + configuration.getSessionTimeout());
        Console.println("connectionTimeout: " + configuration.getConnectionTimeout());
        Console.println("baseSleepTimeMs: " + configuration.getBaseSleepTimeMs());
        Console.println("maxRetries: " + configuration.getMaxRetries());
        Console.println("namespace: " + configuration.getNamespace());
        Console.println("digest: " + configuration.getDigest());
    }
}