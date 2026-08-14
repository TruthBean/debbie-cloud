package com.truthbean.cloud.gateway.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.cloud.gateway.GatewayConfiguration;
import com.truthbean.cloud.gateway.GatewayRouteLocator;
import com.truthbean.cloud.gateway.GatewayProxyHandler;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class GatewayConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject GatewayConfiguration configuration) {
        Console.println(configuration);
        Console.println("connectTimeout: " + configuration.getConnectTimeout());
        Console.println("readTimeout: " + configuration.getReadTimeout());
        Console.println("maxRetries: " + configuration.getMaxRetries());
    }

    @Test
    public void testRouteLocator(@BeanInject GatewayRouteLocator routeLocator) {
        Console.println(routeLocator);
        Console.println("routes: " + routeLocator.getRoutes().size());
    }

    @Test
    public void testProxyHandler(@BeanInject GatewayProxyHandler proxyHandler) {
        Console.println(proxyHandler);
    }
}