/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.shardingsphere;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.mode.repository.standalone.StandalonePersistRepositoryConfiguration;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ShardingSphereDebbieDataSourceFactory implements DataSourceFactory {

    private javax.sql.DataSource dataSource;
    private DataSourceDriverName driverName;
    private String name;

    private final ShardingSphereConfiguration configuration;

    public ShardingSphereDebbieDataSourceFactory(ShardingSphereConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T extends DataSourceConfiguration> boolean support(T configuration) {
        return true;
    }

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        this.dataSource = dataSource;
        this.name = "defaultShardingSphereDataSourceFactory";
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        try {
            this.dataSource = createDataSource(this.configuration);
            this.name = this.configuration.getCategory() + "ShardingSphereDebbieDataSourceFactory";
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to create ShardingSphere data source", e);
        }
        return this;
    }

    private javax.sql.DataSource createDataSource(ShardingSphereConfiguration config) throws SQLException, IOException {
        if (config.getYamlConfigLocation() != null && !config.getYamlConfigLocation().isBlank()) {
            return createFromYaml(config.getYamlConfigLocation());
        }
        return createFromModeConfig(config);
    }

    private javax.sql.DataSource createFromYaml(String yamlLocation) throws IOException, SQLException {
        File yamlFile;
        if (yamlLocation.startsWith("classpath:")) {
            String path = yamlLocation.substring("classpath:".length());
            var resource = Thread.currentThread().getContextClassLoader().getResource(path);
            if (resource == null) {
                throw new IllegalArgumentException("YAML config file not found: " + yamlLocation);
            }
            yamlFile = new File(resource.getFile());
        } else {
            yamlFile = new File(yamlLocation);
        }
        if (!yamlFile.exists()) {
            throw new IllegalArgumentException("YAML config file not found: " + yamlFile.getAbsolutePath());
        }
        return YamlShardingSphereDataSourceFactory.createDataSource(yamlFile);
    }

    private javax.sql.DataSource createFromModeConfig(ShardingSphereConfiguration config) throws SQLException {
        ModeConfiguration modeConfig = createModeConfiguration(config);
        Properties props = new Properties();
        if (config.isSqlShow()) {
            props.setProperty("sql-show", "true");
        }
        if (config.isSqlSimple()) {
            props.setProperty("sql-simple", "true");
        }
        return ShardingSphereDataSourceFactory.createDataSource(
                config.getDatabaseName(),
                modeConfig,
                Collections.<String, DataSource>emptyMap(),
                Collections.<RuleConfiguration>emptyList(),
                props
        );
    }

    private ModeConfiguration createModeConfiguration(ShardingSphereConfiguration config) {
        String modeType = config.getModeType() != null ? config.getModeType() : "standalone";
        if ("cluster".equalsIgnoreCase(modeType)) {
            return createClusterModeConfiguration(config);
        }
        return createStandaloneModeConfiguration(config);
    }

    private ModeConfiguration createStandaloneModeConfiguration(ShardingSphereConfiguration config) {
        var props = new Properties();
        String filePath = config.getModeRepositoryFilePath() != null ? config.getModeRepositoryFilePath() : ".shardingSphere";
        props.setProperty("path", filePath);
        var repositoryConfig = new StandalonePersistRepositoryConfiguration(
                config.getModeRepositoryType() != null ? config.getModeRepositoryType() : "File",
                props
        );
        return new ModeConfiguration("standalone", repositoryConfig);
    }

    private ModeConfiguration createClusterModeConfiguration(ShardingSphereConfiguration config) {
        var props = new Properties();
        if (config.getModeClusterNamespace() != null) {
            props.setProperty("namespace", config.getModeClusterNamespace());
        }
        if (config.getModeClusterServerLists() != null) {
            props.setProperty("server-lists", config.getModeClusterServerLists());
        }
        var repositoryConfig = new StandalonePersistRepositoryConfiguration("ZooKeeper", props);
        return new ModeConfiguration("cluster", repositoryConfig);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public javax.sql.DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public void close() {
        if (dataSource != null) {
            try {
                if (dataSource instanceof AutoCloseable closeable) {
                    closeable.close();
                }
            } catch (Exception e) {
                LOGGER.error("Failed to close ShardingSphere data source", e);
            }
        }
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        close();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardingSphereDebbieDataSourceFactory.class);
}
