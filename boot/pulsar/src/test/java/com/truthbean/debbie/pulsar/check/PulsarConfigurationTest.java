package com.truthbean.debbie.pulsar.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.pulsar.PulsarClientFactory;
import com.truthbean.debbie.pulsar.PulsarConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class PulsarConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject PulsarConfiguration configuration) {
        Console.println(configuration);
        Console.println("serviceUrl: " + configuration.getServiceUrl());
        Console.println("producerName: " + configuration.getProducerName());
        Console.println("consumerName: " + configuration.getConsumerName());
        Console.println("sendTimeout: " + configuration.getSendTimeout());
        Console.println("operationTimeout: " + configuration.getOperationTimeout());
        Console.println("connectionTimeout: " + configuration.getConnectionTimeout());
    }

    @Test
    public void testClientFactory(@BeanInject PulsarClientFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
    }
}