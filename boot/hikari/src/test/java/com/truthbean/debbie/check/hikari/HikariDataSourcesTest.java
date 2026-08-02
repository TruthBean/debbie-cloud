package com.truthbean.debbie.check.hikari;

import com.truthbean.Console;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.hikari.HikariConfiguration;
import com.truthbean.debbie.hikari.HikariDataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.repository.RepositoryCallback;
import com.truthbean.debbie.jdbc.repository.RepositoryHandler;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

@DebbieApplicationTest
public class HikariDataSourcesTest {

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
    public void testLargeQuery(@BeanInject HikariConfiguration configuration) {
        DataSourceFactory factory = DataSourceFactory.loadFactory(configuration);
        factory.getTransaction();
        RepositoryHandler repositoryHandler = RepositoryHandler.INSTANCE;
        while (true) {
            TransactionManager.offer(factory.getTransaction());
            var r = RepositoryCallback.actionTransactional(transactionInfo -> {
                try {
                    return repositoryHandler.queryOne(LOGGER, transactionInfo, "select now()");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
            Console.println(r);
        }
    }

    @Test
    public void testDataSourceConfiguration(@BeanInject DataSourceConfiguration configuration) {
        Console.println(configuration);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSourcesTest.class);
}
