/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.properties.ConfigurationTypeNotMatchedException;

import javax.sql.DataSource;

/**
 * @author truthbean
 * @since 0.6.3
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private DruidDataSource druidDataSource;
    private DataSourceDriverName driverName;

    private String name;

    @Override
    public <T extends DataSourceConfiguration> boolean support(T configuration) {
        return configuration instanceof DruidConfiguration;
    }

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        if (dataSource instanceof DruidDataSource) {
            druidDataSource = (DruidDataSource) dataSource;
        }
        this.name = "defaultDruidDataSourceFactory";
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        if (configuration instanceof DruidConfiguration druidConfiguration) {
            DruidDataSource datasource = druidConfiguration.getDruidDataSource();
            DataSourceDriverName driverName = configuration.getDriverName();
            this.driverName = driverName;

            String driverClassName = druidConfiguration.getDriverClassName();
            if (driverClassName != null && !driverClassName.isBlank()) {
                datasource.setDriverClassName(driverClassName);
            } else if (driverName != null) {
                datasource.setDriverClassName(driverName.getDriverName());
            }

            String url = druidConfiguration.getDruidUrl();
            if (url != null && !url.isBlank()) {
                datasource.setUrl(url);
            } else {
                datasource.setUrl(configuration.getUrl());
            }

            String username = druidConfiguration.getUsername();
            if (username != null && !username.isBlank()) {
                datasource.setUsername(username);
            } else {
                datasource.setUsername(configuration.getUser());
            }

            String password = druidConfiguration.getDruidPassword();
            if (password != null && !password.isBlank()) {
                datasource.setPassword(password);
            } else {
                datasource.setPassword(configuration.getPassword());
            }

            int defaultTransactionIsolation = druidConfiguration.getDefaultTransactionIsolation();
            if (defaultTransactionIsolation == -1) {
                datasource.setDefaultTransactionIsolation(druidConfiguration.getDefaultTransactionIsolationLevel().getLevel());
            }

            druidDataSource = datasource;
            this.name = configuration.getCategory() + "DruidDataSourceFactory";
        } else {
            throw new ConfigurationTypeNotMatchedException();
        }

        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataSource getDataSource() {
        return druidDataSource;
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public void close() {
        druidDataSource.close();
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        druidDataSource.close();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private static final Logger logger = LoggerFactory.getLogger(DruidDataSourceFactory.class);
}