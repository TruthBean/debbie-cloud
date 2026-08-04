/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.c3p0;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.properties.ConfigurationTypeNotMatchedException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class C3p0DataSourceFactory implements DataSourceFactory {

    private ComboPooledDataSource comboPooledDataSource;
    private DataSourceDriverName driverName;

    private String name;

    @Override
    public <T extends DataSourceConfiguration> boolean support(T configuration) {
        return configuration instanceof C3p0Configuration;
    }

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        if (dataSource instanceof ComboPooledDataSource) {
            comboPooledDataSource = (ComboPooledDataSource) dataSource;
        }
        this.name = "defaultC3p0DataSourceFactory";
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        if (configuration instanceof C3p0Configuration c3p0Configuration) {
            ComboPooledDataSource datasource = c3p0Configuration.getComboPooledDataSource();
            DataSourceDriverName driverName = configuration.getDriverName();
            if (c3p0Configuration.getDriverClassName() == null && driverName != null) {
                this.driverName = driverName;
                try {
                    datasource.setDriverClass(driverName.getDriverName());
                } catch (Exception e) {
                    getLogger().error("Failed to set driver class for c3p0 ComboPooledDataSource", e);
                }
            }
            if (c3p0Configuration.getC3p0Url() == null || c3p0Configuration.getC3p0Url().isBlank()) {
                datasource.setJdbcUrl(configuration.getUrl());
            }
            if (c3p0Configuration.getUsername() == null || c3p0Configuration.getUsername().isBlank()) {
                datasource.setUser(configuration.getUser());
            }
            if (c3p0Configuration.getC3p0Password() == null || c3p0Configuration.getC3p0Password().isBlank()) {
                datasource.setPassword(configuration.getPassword());
            }
            comboPooledDataSource = datasource;
            this.name = configuration.getCategory() + "C3p0DataSourceFactory";
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
        return comboPooledDataSource;
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public void close() {
        comboPooledDataSource.close();
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        close();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private static final Logger logger = LoggerFactory.getLogger(C3p0DataSourceFactory.class);
}
