package com.truthbean.debbie.mybatisplus.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.mybatisplus.MybatisPlusConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class MybatisPlusConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject MybatisPlusConfiguration configuration) {
        Console.println(configuration);
        Console.println("banner: " + configuration.isBanner());
        Console.println("checkConfigLocation: " + configuration.isCheckConfigLocation());
        Console.println("configLocation: " + configuration.getConfigLocation());
        Console.println("mapperLocations: " + configuration.getMapperLocations());
        Console.println("dbConfigMapUnderscoreToCamelCase: " + configuration.isDbConfigMapUnderscoreToCamelCase());
        Console.println("dbConfigTablePrefix: " + configuration.getDbConfigTablePrefix());
        Console.println("dbConfigIdType: " + configuration.getDbConfigIdType());
        Console.println("dbConfigLogicDeleteField: " + configuration.getDbConfigLogicDeleteField());
        Console.println("dbConfigLogicDeleteValue: " + configuration.getDbConfigLogicDeleteValue());
        Console.println("dbConfigLogicNotDeleteValue: " + configuration.getDbConfigLogicNotDeleteValue());
        Console.println("dbConfigUpdateStrategy: " + configuration.getDbConfigUpdateStrategy());
        Console.println("dbConfigInsertStrategy: " + configuration.getDbConfigInsertStrategy());
        Console.println("dbConfigWhereStrategy: " + configuration.getDbConfigWhereStrategy());
        Console.println("paginationMaxLimit: " + configuration.getPaginationMaxLimit());
        Console.println("paginationOverflow: " + configuration.isPaginationOverflow());
    }
}