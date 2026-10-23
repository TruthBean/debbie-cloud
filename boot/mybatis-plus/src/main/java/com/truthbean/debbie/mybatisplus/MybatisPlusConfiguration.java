/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatisplus;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MybatisPlusConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Whether to enable banner on startup.
     * Default: true
     */
    private boolean banner;

    /**
     * Whether to check config location exists.
     * Default: false
     */
    private boolean checkConfigLocation;

    /**
     * Mybatis config xml location.
     */
    private String configLocation;

    /**
     * Mapper locations (semicolon-separated).
     */
    private String mapperLocations;

    // ======================== GlobalConfig.DbConfig ========================

    /**
     * Table underline to camel case.
     * Default: true
     */
    private boolean dbConfigMapUnderscoreToCamelCase;

    /**
     * Global table prefix.
     */
    private String dbConfigTablePrefix;

    /**
     * ID type (AUTO/NONE/INPUT/ASSIGN_ID/ASSIGN_UUID).
     * Default: ASSIGN_ID
     */
    private String dbConfigIdType;

    /**
     * Logic delete field name.
     */
    private String dbConfigLogicDeleteField;

    /**
     * Logic delete value.
     */
    private String dbConfigLogicDeleteValue;

    /**
     * Logic not delete value.
     */
    private String dbConfigLogicNotDeleteValue;

    /**
     * Whether to auto fill update time on insert.
     * Default: true
     */
    private String dbConfigUpdateStrategy;

    /**
     * Insert strategy.
     * Default: NOT_NULL
     */
    private String dbConfigInsertStrategy;

    /**
     * Where strategy.
     * Default: NOT_NULL
     */
    private String dbConfigWhereStrategy;

    // ======================== Pagination ========================

    /**
     * Max limit for pagination.
     * Default: -1 (no limit)
     */
    private int paginationMaxLimit;

    /**
     * Overflow page.
     * Default: false
     */
    private boolean paginationOverflow;

    // ======================== Getter/Setter ========================

    public boolean isBanner() { return banner; }
    public void setBanner(boolean banner) { this.banner = banner; }

    public boolean isCheckConfigLocation() { return checkConfigLocation; }
    public void setCheckConfigLocation(boolean checkConfigLocation) { this.checkConfigLocation = checkConfigLocation; }

    public String getConfigLocation() { return configLocation; }
    public void setConfigLocation(String configLocation) { this.configLocation = configLocation; }

    public String getMapperLocations() { return mapperLocations; }
    public void setMapperLocations(String mapperLocations) { this.mapperLocations = mapperLocations; }

    public boolean isDbConfigMapUnderscoreToCamelCase() { return dbConfigMapUnderscoreToCamelCase; }
    public void setDbConfigMapUnderscoreToCamelCase(boolean dbConfigMapUnderscoreToCamelCase) { this.dbConfigMapUnderscoreToCamelCase = dbConfigMapUnderscoreToCamelCase; }

    public String getDbConfigTablePrefix() { return dbConfigTablePrefix; }
    public void setDbConfigTablePrefix(String dbConfigTablePrefix) { this.dbConfigTablePrefix = dbConfigTablePrefix; }

    public String getDbConfigIdType() { return dbConfigIdType; }
    public void setDbConfigIdType(String dbConfigIdType) { this.dbConfigIdType = dbConfigIdType; }

    public String getDbConfigLogicDeleteField() { return dbConfigLogicDeleteField; }
    public void setDbConfigLogicDeleteField(String dbConfigLogicDeleteField) { this.dbConfigLogicDeleteField = dbConfigLogicDeleteField; }

    public String getDbConfigLogicDeleteValue() { return dbConfigLogicDeleteValue; }
    public void setDbConfigLogicDeleteValue(String dbConfigLogicDeleteValue) { this.dbConfigLogicDeleteValue = dbConfigLogicDeleteValue; }

    public String getDbConfigLogicNotDeleteValue() { return dbConfigLogicNotDeleteValue; }
    public void setDbConfigLogicNotDeleteValue(String dbConfigLogicNotDeleteValue) { this.dbConfigLogicNotDeleteValue = dbConfigLogicNotDeleteValue; }

    public String getDbConfigUpdateStrategy() { return dbConfigUpdateStrategy; }
    public void setDbConfigUpdateStrategy(String dbConfigUpdateStrategy) { this.dbConfigUpdateStrategy = dbConfigUpdateStrategy; }

    public String getDbConfigInsertStrategy() { return dbConfigInsertStrategy; }
    public void setDbConfigInsertStrategy(String dbConfigInsertStrategy) { this.dbConfigInsertStrategy = dbConfigInsertStrategy; }

    public String getDbConfigWhereStrategy() { return dbConfigWhereStrategy; }
    public void setDbConfigWhereStrategy(String dbConfigWhereStrategy) { this.dbConfigWhereStrategy = dbConfigWhereStrategy; }

    public int getPaginationMaxLimit() { return paginationMaxLimit; }
    public void setPaginationMaxLimit(int paginationMaxLimit) { this.paginationMaxLimit = paginationMaxLimit; }

    public boolean isPaginationOverflow() { return paginationOverflow; }
    public void setPaginationOverflow(boolean paginationOverflow) { this.paginationOverflow = paginationOverflow; }

    // ======================== DebbieConfiguration ========================

    @Override
    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() { return (T) this; }

    @Override
    public void close() {}
}