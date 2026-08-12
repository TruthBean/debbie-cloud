package com.truthbean.debbie.dubbo.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.dubbo.DubboConfiguration;
import com.truthbean.debbie.dubbo.DubboServiceFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class DubboConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject DubboConfiguration configuration) {
        Console.println(configuration);
        Console.println("applicationName: " + configuration.getApplicationName());
        Console.println("applicationOwner: " + configuration.getApplicationOwner());
        Console.println("applicationOrganization: " + configuration.getApplicationOrganization());
        Console.println("protocolName: " + configuration.getProtocolName());
        Console.println("protocolPort: " + configuration.getProtocolPort());
        Console.println("protocolHost: " + configuration.getProtocolHost());
        Console.println("protocolThreadpool: " + configuration.getProtocolThreadpool());
        Console.println("protocolThreads: " + configuration.getProtocolThreads());
        Console.println("registryAddress: " + configuration.getRegistryAddress());
        Console.println("registryProtocol: " + configuration.getRegistryProtocol());
        Console.println("registryGroup: " + configuration.getRegistryGroup());
        Console.println("registryCheck: " + configuration.isRegistryCheck());
        Console.println("registryRegister: " + configuration.isRegistryRegister());
        Console.println("providerTimeout: " + configuration.getProviderTimeout());
        Console.println("providerRetries: " + configuration.getProviderRetries());
        Console.println("providerDelay: " + configuration.getProviderDelay());
        Console.println("consumerTimeout: " + configuration.getConsumerTimeout());
        Console.println("consumerRetries: " + configuration.getConsumerRetries());
        Console.println("consumerCheck: " + configuration.isConsumerCheck());
    }

    @Test
    public void testServiceFactory(@BeanInject DubboServiceFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
        Console.println(factory.getBootstrap());
    }
}