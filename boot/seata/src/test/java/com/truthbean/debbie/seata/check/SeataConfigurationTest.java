package com.truthbean.debbie.seata.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.seata.SeataConfiguration;
import com.truthbean.debbie.seata.SeataTransactionFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class SeataConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject SeataConfiguration configuration) {
        Console.println(configuration);
        Console.println("applicationId: " + configuration.getApplicationId());
        Console.println("txServiceGroup: " + configuration.getTxServiceGroup());
        Console.println("serverAddr: " + configuration.getServerAddr());
    }

    @Test
    public void testTransactionFactory(@BeanInject SeataTransactionFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
    }
}