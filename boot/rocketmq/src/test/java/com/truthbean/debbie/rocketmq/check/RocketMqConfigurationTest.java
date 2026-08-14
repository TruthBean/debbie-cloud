package com.truthbean.debbie.rocketmq.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.rocketmq.RocketMqClientFactory;
import com.truthbean.debbie.rocketmq.RocketMqConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class RocketMqConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject RocketMqConfiguration configuration) {
        Console.println(configuration);
        Console.println("namesrvAddr: " + configuration.getNamesrvAddr());
        Console.println("producerGroup: " + configuration.getProducerGroup());
        Console.println("consumerGroup: " + configuration.getConsumerGroup());
        Console.println("sendTimeout: " + configuration.getSendTimeout());
        Console.println("maxMessageSize: " + configuration.getMaxMessageSize());
        Console.println("retryTimesWhenSendFailed: " + configuration.getRetryTimesWhenSendFailed());
        Console.println("pollTimeout: " + configuration.getPollTimeout());
    }

    @Test
    public void testClientFactory(@BeanInject RocketMqClientFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
    }
}