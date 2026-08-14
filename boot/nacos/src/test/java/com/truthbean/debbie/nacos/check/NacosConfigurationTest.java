package com.truthbean.debbie.nacos.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.nacos.NacosClientFactory;
import com.truthbean.debbie.nacos.NacosConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class NacosConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject NacosConfiguration configuration) {
        Console.println(configuration);
        Console.println("serverAddr: " + configuration.getServerAddr());
        Console.println("namespace: " + configuration.getNamespace());
        Console.println("username: " + configuration.getUsername());
        Console.println("password: " + configuration.getPassword());
    }

    @Test
    public void testClientFactory(@BeanInject NacosClientFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
    }
}