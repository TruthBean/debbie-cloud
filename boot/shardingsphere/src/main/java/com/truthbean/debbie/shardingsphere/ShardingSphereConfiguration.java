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

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.shardingsphere")
public class ShardingSphereConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Path to the ShardingSphere YAML configuration file.
     * e.g. "classpath:shardingsphere-config.yaml"
     */
    @PropertyInject("yaml-config-location")
    private String yamlConfigLocation;

    /**
     * Database name for ShardingSphere data source.
     * Default: "sharding_db"
     */
    @PropertyInject(value = "database-name", defaultValue = "sharding_db")
    private String databaseName;

    // ======================== Mode Configuration ========================

    /**
     * ShardingSphere mode type: "standalone" or "cluster".
     * Default: "standalone"
     */
    @PropertyInject(value = "mode-type", defaultValue = "standalone")
    private String modeType;

    /**
     * Repository type for standalone mode: "file" or "database".
     * Default: "file"
     */
    @PropertyInject(value = "mode-repository-type", defaultValue = "file")
    private String modeRepositoryType;

    /**
     * File path for standalone file-based persistence.
     * Default: ".shardingSphere"
     */
    @PropertyInject(value = "mode-repository-file-path", defaultValue = ".shardingSphere")
    private String modeRepositoryFilePath;

    /**
     * Whether to overwrite the configuration with the persisted one.
     * Default: false
     */
    @PropertyInject(value = "mode-overwrite", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean modeOverwrite;

    // ======================== Cluster Mode (optional) ========================

    /**
     * Cluster mode namespace.
     */
    @PropertyInject("mode-cluster-namespace")
    private String modeClusterNamespace;

    /**
     * Cluster mode server list, e.g. "localhost:2181".
     */
    @PropertyInject("mode-cluster-server-lists")
    private String modeClusterServerLists;

    // ======================== Props ========================

    /**
     * Whether to show SQL logs.
     * Default: false
     */
    @PropertyInject(value = "sql-show", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean sqlShow;

    /**
     * Whether to show SQL details.
     * Default: false
     */
    @PropertyInject(value = "sql-simple", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean sqlSimple;

    // ======================== Getter/Setter ========================

    public String getYamlConfigLocation() {
        return yamlConfigLocation;
    }

    public void setYamlConfigLocation(String yamlConfigLocation) {
        this.yamlConfigLocation = yamlConfigLocation;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public String getModeRepositoryType() {
        return modeRepositoryType;
    }

    public void setModeRepositoryType(String modeRepositoryType) {
        this.modeRepositoryType = modeRepositoryType;
    }

    public String getModeRepositoryFilePath() {
        return modeRepositoryFilePath;
    }

    public void setModeRepositoryFilePath(String modeRepositoryFilePath) {
        this.modeRepositoryFilePath = modeRepositoryFilePath;
    }

    public boolean isModeOverwrite() {
        return modeOverwrite;
    }

    public void setModeOverwrite(boolean modeOverwrite) {
        this.modeOverwrite = modeOverwrite;
    }

    public String getModeClusterNamespace() {
        return modeClusterNamespace;
    }

    public void setModeClusterNamespace(String modeClusterNamespace) {
        this.modeClusterNamespace = modeClusterNamespace;
    }

    public String getModeClusterServerLists() {
        return modeClusterServerLists;
    }

    public void setModeClusterServerLists(String modeClusterServerLists) {
        this.modeClusterServerLists = modeClusterServerLists;
    }

    public boolean isSqlShow() {
        return sqlShow;
    }

    public void setSqlShow(boolean sqlShow) {
        this.sqlShow = sqlShow;
    }

    public boolean isSqlSimple() {
        return sqlSimple;
    }

    public void setSqlSimple(boolean sqlSimple) {
        this.sqlSimple = sqlSimple;
    }

    // ======================== DebbieConfiguration ========================

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getProfile() {
        return EnvironmentDepositoryHolder.DEFAULT_PROFILE;
    }

    @Override
    public String getCategory() {
        return EnvironmentDepositoryHolder.DEFAULT_CATEGORY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        return (T) this;
    }

    @Override
    public void close() {
    }
}