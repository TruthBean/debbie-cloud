package com.truthbean.debbie.tomcat.jdbc.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.tomcat.jdbc.TomcatJdbcConfiguration;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class TomcatJdbcTest {

    @Test
    public void testConfiguration() {
        ApplicationFactory applicationFactory = ApplicationFactory.configure(TomcatJdbcTest.class);
        ApplicationContext context = applicationFactory.getApplicationContext();
        GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();

        TomcatJdbcConfiguration configuration = globalBeanFactory.factory(TomcatJdbcConfiguration.class);
        Console.println(configuration);
        Console.println("driverClassName: " + configuration.getDriverClassName());
        Console.println("url: " + configuration.getJdbcUrl());
        Console.println("username: " + configuration.getUsername());
        Console.println("initialSize: " + configuration.getInitialSize());
        Console.println("maxActive: " + configuration.getMaxActive());
        Console.println("maxIdle: " + configuration.getMaxIdle());
        Console.println("minIdle: " + configuration.getMinIdle());
        Console.println("maxWait: " + configuration.getMaxWait());
        Console.println("validationQuery: " + configuration.getValidationQuery());
        Console.println("testOnBorrow: " + configuration.isTestOnBorrow());
        Console.println("timeBetweenEvictionRunsMillis: " + configuration.getTimeBetweenEvictionRunsMillis());

        DataSourceFactory factory = globalBeanFactory.factory("dataSourceFactory");
        Console.println(factory);
        Console.println("dataSource: " + factory.getDataSource());

        try {
            Connection connection = factory.getConnection();
            Console.println("connection: " + connection);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            applicationFactory.release();
        }
    }
}