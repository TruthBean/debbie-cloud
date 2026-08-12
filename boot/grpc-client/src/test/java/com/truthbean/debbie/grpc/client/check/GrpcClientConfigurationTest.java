package com.truthbean.debbie.grpc.client.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.grpc.client.GrpcClientConfiguration;
import com.truthbean.debbie.grpc.client.GrpcClientFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class GrpcClientConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject GrpcClientConfiguration configuration) {
        Console.println(configuration);
        Console.println("host: " + configuration.getHost());
        Console.println("port: " + configuration.getPort());
        Console.println("usePlaintext: " + configuration.isUsePlaintext());
        Console.println("maxInboundMessageSize: " + configuration.getMaxInboundMessageSize());
        Console.println("keepAliveTime: " + configuration.getKeepAliveTime());
        Console.println("keepAliveTimeout: " + configuration.getKeepAliveTimeout());
        Console.println("keepAliveWithoutCalls: " + configuration.isKeepAliveWithoutCalls());
    }

    @Test
    public void testClientFactory(@BeanInject GrpcClientFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
        Console.println(factory.getChannel());
    }
}