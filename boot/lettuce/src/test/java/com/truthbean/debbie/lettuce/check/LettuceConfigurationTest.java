package com.truthbean.debbie.lettuce.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.lettuce.LettuceClientFactory;
import com.truthbean.debbie.lettuce.LettuceConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class LettuceConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject LettuceConfiguration configuration) {
        Console.println(configuration);
        Console.println("host: " + configuration.getHost());
        Console.println("port: " + configuration.getPort());
        Console.println("password: " + configuration.getPassword());
        Console.println("database: " + configuration.getDatabase());
        Console.println("timeout: " + configuration.getTimeout());
        Console.println("clientName: " + configuration.getClientName());
        Console.println("autoReconnect: " + configuration.isAutoReconnect());
        Console.println("ssl: " + configuration.isSsl());
    }

    @Test
    public void testClientFactory(@BeanInject LettuceClientFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
        Console.println(factory.getClient());
    }
}