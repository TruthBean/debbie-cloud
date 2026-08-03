package com.truthbean.debbie.druid.check;

import com.truthbean.Console;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

@DebbieApplicationTest
public class DruidDataSourcesTest {

    @Test
    public void testDataSource(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        Console.println(factory);
        try {
            Connection connection = factory.getConnection();
            Console.println(connection);
            Thread.sleep(1000);
            connection.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDataSourceConfiguration(@BeanInject DataSourceConfiguration configuration) {
        Console.println(configuration);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DruidDataSourcesTest.class);
}
