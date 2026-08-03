/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.dbcp2;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.properties.ConfigurationTypeNotMatchedException;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

/**
 * @author truthbean
 * @since 0.6.3
 */
public class Dbcp2DataSourceFactory implements DataSourceFactory {

    private BasicDataSource basicDataSource;
    private DataSourceDriverName driverName;

    private String name;

    @Override
    public <T extends DataSourceConfiguration> boolean support(T configuration) {
        return configuration instanceof Dbcp2Configuration;
    }

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        if (dataSource instanceof BasicDataSource) {
            basicDataSource = (BasicDataSource) dataSource;
        }
        this.name = "defaultDbcp2DataSourceFactory";
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        if (configuration instanceof Dbcp2Configuration dbcp2Configuration) {
            BasicDataSource datasource = dbcp2Configuration.getBasicDataSource();
            DataSourceDriverName driverName = configuration.getDriverName();
            if (dbcp2Configuration.getDriverClassName() == null && driverName != null) {
                this.driverName = driverName;
                datasource.setDriverClassName(driverName.getDriverName());
            }
            if (dbcp2Configuration.getDbcp2Url() == null || dbcp2Configuration.getDbcp2Url().isBlank()) {
                datasource.setUrl(configuration.getUrl());
            }
            if (dbcp2Configuration.getUsername() == null || dbcp2Configuration.getUsername().isBlank()) {
                datasource.setUsername(configuration.getUser());
            }
            if (dbcp2Configuration.getDbcp2Password() == null || dbcp2Configuration.getDbcp2Password().isBlank()) {
                datasource.setPassword(configuration.getPassword());
            }
            basicDataSource = datasource;
            this.name = configuration.getCategory() + "Dbcp2DataSourceFactory";
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
        return basicDataSource;
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public void close() {
        try {
            basicDataSource.close();
        } catch (Exception e) {
            getLogger().error("Failed to close DBCP2 BasicDataSource", e);
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

    private static final Logger logger = LoggerFactory.getLogger(Dbcp2DataSourceFactory.class);
}