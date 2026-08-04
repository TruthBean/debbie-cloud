/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tomcat.jdbc;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.properties.ConfigurationTypeNotMatchedException;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.sql.DataSource;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class TomcatJdbcDataSourceFactory implements DataSourceFactory {
    private org.apache.tomcat.jdbc.pool.DataSource tomcatJdbcDataSource;
    private DataSourceDriverName driverName;

    private String name;

    @Override
    public <T extends DataSourceConfiguration> boolean support(T configuration) {
        return configuration instanceof TomcatJdbcConfiguration;
    }

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            tomcatJdbcDataSource = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
        }
        this.name = "defaultTomcatJdbcDataSourceFactory";
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        if (configuration instanceof TomcatJdbcConfiguration tomcatJdbcConfiguration) {
            PoolProperties poolProperties = tomcatJdbcConfiguration.getPoolProperties();
            DataSourceDriverName driverName = configuration.getDriverName();
            if (tomcatJdbcConfiguration.getDriverClassName() == null && driverName != null) {
                this.driverName = driverName;
                poolProperties.setDriverClassName(driverName.getDriverName());
            }
            if (tomcatJdbcConfiguration.getJdbcUrl() == null || tomcatJdbcConfiguration.getJdbcUrl().isBlank()) {
                poolProperties.setUrl(configuration.getUrl());
            }
            if (tomcatJdbcConfiguration.getUsername() == null || tomcatJdbcConfiguration.getUsername().isBlank()) {
                poolProperties.setUsername(configuration.getUser());
            }
            if (tomcatJdbcConfiguration.getPassword() == null || tomcatJdbcConfiguration.getPassword().isBlank()) {
                poolProperties.setPassword(configuration.getPassword());
            }
            if (tomcatJdbcConfiguration.getDefaultAutoCommit() == null) {
                poolProperties.setDefaultAutoCommit(configuration.getAutoCommit());
            }
            if (tomcatJdbcConfiguration.getDefaultTransactionIsolation() == null) {
                poolProperties.setDefaultTransactionIsolation(configuration.getDefaultTransactionIsolationLevel().getLevel());
            }
            tomcatJdbcDataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
            this.name = configuration.getCategory() + "TomcatJdbcDataSourceFactory";
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
        return tomcatJdbcDataSource;
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public void close() {
        if (tomcatJdbcDataSource != null) {
            tomcatJdbcDataSource.close();
        }
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        close();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private static final Logger logger = LoggerFactory.getLogger(TomcatJdbcDataSourceFactory.class);
}