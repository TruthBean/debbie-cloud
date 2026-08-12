package com.truthbean.debbie.grpc.server.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.grpc.server.GrpcServerConfiguration;
import com.truthbean.debbie.grpc.server.GrpcServerFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class GrpcServerConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject GrpcServerConfiguration configuration) {
        Console.println(configuration);
        Console.println("port: " + configuration.getPort());
        Console.println("maxInboundMessageSize: " + configuration.getMaxInboundMessageSize());
        Console.println("maxInboundMetadataSize: " + configuration.getMaxInboundMetadataSize());
    }

    @Test
    public void testServerFactory(@BeanInject GrpcServerFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
        Console.println(factory.getServer());
    }
}