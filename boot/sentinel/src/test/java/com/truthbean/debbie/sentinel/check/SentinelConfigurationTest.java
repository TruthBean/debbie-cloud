package com.truthbean.debbie.sentinel.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.sentinel.SentinelConfiguration;
import com.truthbean.debbie.sentinel.SentinelManagerFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class SentinelConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject SentinelConfiguration configuration) {
        Console.println(configuration);
        Console.println("logDir: " + configuration.getLogDir());
        Console.println("logNamePrefix: " + configuration.getLogNamePrefix());
        Console.println("charset: " + configuration.getCharset());
    }

    @Test
    public void testManagerFactory(@BeanInject SentinelManagerFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
    }
}