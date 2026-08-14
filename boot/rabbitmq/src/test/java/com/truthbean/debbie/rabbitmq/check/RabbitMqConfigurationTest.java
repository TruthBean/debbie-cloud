package com.truthbean.debbie.rabbitmq.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.rabbitmq.RabbitMqClientFactory;
import com.truthbean.debbie.rabbitmq.RabbitMqConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class RabbitMqConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject RabbitMqConfiguration configuration) {
        Console.println(configuration);
        Console.println("host: " + configuration.getHost());
        Console.println("port: " + configuration.getPort());
        Console.println("username: " + configuration.getUsername());
        Console.println("password: " + configuration.getPassword());
        Console.println("virtualHost: " + configuration.getVirtualHost());
        Console.println("connectionTimeout: " + configuration.getConnectionTimeout());
    }

    @Test
    public void testClientFactory(@BeanInject RabbitMqClientFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
    }
}