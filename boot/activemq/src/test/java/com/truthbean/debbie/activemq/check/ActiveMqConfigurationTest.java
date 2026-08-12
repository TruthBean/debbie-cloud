package com.truthbean.debbie.activemq.check;

import com.truthbean.Console;
import com.truthbean.debbie.activemq.ActiveMqConfiguration;
import com.truthbean.debbie.activemq.ActiveMqConnectionFactory;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class ActiveMqConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject ActiveMqConfiguration configuration) {
        Console.println(configuration);
        Console.println("brokerUrl: " + configuration.getBrokerUrl());
        Console.println("username: " + configuration.getUsername());
        Console.println("password: " + configuration.getPassword());
        Console.println("maxConnections: " + configuration.getMaxConnections());
        Console.println("useAsyncSend: " + configuration.isUseAsyncSend());
        Console.println("alwaysSyncSend: " + configuration.isAlwaysSyncSend());
        Console.println("closeTimeout: " + configuration.getCloseTimeout());
        Console.println("producerWindowSize: " + configuration.getProducerWindowSize());
        Console.println("dispatchAsync: " + configuration.isDispatchAsync());
        Console.println("redeliveryMaxRedeliveries: " + configuration.getRedeliveryMaxRedeliveries());
        Console.println("redeliveryInitialDelay: " + configuration.getRedeliveryInitialDelay());
        Console.println("redeliveryBackOffMultiplier: " + configuration.getRedeliveryBackOffMultiplier());
        Console.println("redeliveryUseExponentialBackOff: " + configuration.isRedeliveryUseExponentialBackOff());
    }

    @Test
    public void testConnectionFactory(@BeanInject ActiveMqConnectionFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
        Console.println(factory.getConnectionFactory());
    }
}