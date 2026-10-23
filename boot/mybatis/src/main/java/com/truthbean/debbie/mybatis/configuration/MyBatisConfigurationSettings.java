/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis.configuration;

import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.*;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * http://www.mybatis.org/mybatis-3/configuration.html#settings
 *
 * @author truthbean
 * @since 0.0.2
 */
public class MyBatisConfigurationSettings {

    private boolean cacheEnabled = true;

    private boolean lazyLoadingEnabled = false;

    private boolean aggressiveLazyLoading = false;

    private boolean multipleResultSetsEnabled = true;

    private boolean useColumnLabel = true;

    private boolean useGeneratedKeys = false;

    private AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;

    private AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior = AutoMappingUnknownColumnBehavior.NONE;

    private ExecutorType defaultExecutorType = ExecutorType.SIMPLE;

    private Integer defaultStatementTimeout = null;

    private Integer defaultFetchSize = null;

    private boolean safeRowBoundsEnabled = false;

    private boolean safeResultHandlerEnabled = true;

    private boolean mapUnderscoreToCamelCase = false;

    private LocalCacheScope localCacheScope = LocalCacheScope.SESSION;

    private JdbcType jdbcTypeForNull = JdbcType.OTHER;

    private Set<String> lazyLoadTriggerMethods = new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString"));

    private Class<? extends LanguageDriver> defaultScriptingLanguage = XMLLanguageDriver.class;

    private Class<? extends TypeHandler> defaultEnumTypeHandler = EnumTypeHandler.class;

    private boolean callSettersOnNulls;

    private boolean returnInstanceForEmptyRow;

    private String logPrefix;

    private Class<? extends Log> logImpl = null;

    private ProxyFactory proxyFactory = new JavassistProxyFactory(); // #224 Using internal Javassist instead of OGNL

    private Class<? extends VFS> vfsImpl = null;

    private boolean useActualParamName = true;

    private Class<?> configurationFactory = null;

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public boolean isLazyLoadingEnabled() {
        return lazyLoadingEnabled;
    }

    public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
        this.lazyLoadingEnabled = lazyLoadingEnabled;
    }

    public boolean isAggressiveLazyLoading() {
        return aggressiveLazyLoading;
    }

    public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
        this.aggressiveLazyLoading = aggressiveLazyLoading;
    }

    public boolean isMultipleResultSetsEnabled() {
        return multipleResultSetsEnabled;
    }

    public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
        this.multipleResultSetsEnabled = multipleResultSetsEnabled;
    }

    public boolean isUseColumnLabel() {
        return useColumnLabel;
    }

    public void setUseColumnLabel(boolean useColumnLabel) {
        this.useColumnLabel = useColumnLabel;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public AutoMappingBehavior getAutoMappingBehavior() {
        return autoMappingBehavior;
    }

    public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
        this.autoMappingBehavior = autoMappingBehavior;
    }

    public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
        return autoMappingUnknownColumnBehavior;
    }

    public void setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
        this.autoMappingUnknownColumnBehavior = autoMappingUnknownColumnBehavior;
    }

    public ExecutorType getDefaultExecutorType() {
        return defaultExecutorType;
    }

    public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
        this.defaultExecutorType = defaultExecutorType;
    }

    public Integer getDefaultStatementTimeout() {
        return defaultStatementTimeout;
    }

    public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
        this.defaultStatementTimeout = defaultStatementTimeout;
    }

    public Integer getDefaultFetchSize() {
        return defaultFetchSize;
    }

    public void setDefaultFetchSize(Integer defaultFetchSize) {
        this.defaultFetchSize = defaultFetchSize;
    }

    public boolean isSafeRowBoundsEnabled() {
        return safeRowBoundsEnabled;
    }

    public void setSafeRowBoundsEnabled(boolean safeRowBoundsEnabled) {
        this.safeRowBoundsEnabled = safeRowBoundsEnabled;
    }

    public boolean isSafeResultHandlerEnabled() {
        return safeResultHandlerEnabled;
    }

    public void setSafeResultHandlerEnabled(boolean safeResultHandlerEnabled) {
        this.safeResultHandlerEnabled = safeResultHandlerEnabled;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public LocalCacheScope getLocalCacheScope() {
        return localCacheScope;
    }

    public void setLocalCacheScope(LocalCacheScope localCacheScope) {
        this.localCacheScope = localCacheScope;
    }

    public JdbcType getJdbcTypeForNull() {
        return jdbcTypeForNull;
    }

    public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
        this.jdbcTypeForNull = jdbcTypeForNull;
    }

    public Set<String> getLazyLoadTriggerMethods() {
        return lazyLoadTriggerMethods;
    }

    public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
        this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
    }

    public Class<? extends LanguageDriver> getDefaultScriptingLanguage() {
        return defaultScriptingLanguage;
    }

    public void setDefaultScriptingLanguage(Class<? extends LanguageDriver> defaultScriptingLanguage) {
        this.defaultScriptingLanguage = defaultScriptingLanguage;
    }

    public Class<? extends TypeHandler> getDefaultEnumTypeHandler() {
        return defaultEnumTypeHandler;
    }

    public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> defaultEnumTypeHandler) {
        this.defaultEnumTypeHandler = defaultEnumTypeHandler;
    }

    public boolean isCallSettersOnNulls() {
        return callSettersOnNulls;
    }

    public void setCallSettersOnNulls(boolean callSettersOnNulls) {
        this.callSettersOnNulls = callSettersOnNulls;
    }

    public boolean isReturnInstanceForEmptyRow() {
        return returnInstanceForEmptyRow;
    }

    public void setReturnInstanceForEmptyRow(boolean returnInstanceForEmptyRow) {
        this.returnInstanceForEmptyRow = returnInstanceForEmptyRow;
    }

    public String getLogPrefix() {
        return logPrefix;
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public Class<? extends Log> getLogImpl() {
        return logImpl;
    }

    public void setLogImpl(Class<? extends Log> logImpl) {
        this.logImpl = logImpl;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public Class<? extends VFS> getVfsImpl() {
        return vfsImpl;
    }

    public void setVfsImpl(Class<? extends VFS> vfsImpl) {
        this.vfsImpl = vfsImpl;
    }

    public boolean isUseActualParamName() {
        return useActualParamName;
    }

    public void setUseActualParamName(boolean useActualParamName) {
        this.useActualParamName = useActualParamName;
    }

    public Class<?> getConfigurationFactory() {
        return configurationFactory;
    }

    public void setConfigurationFactory(Class<?> configurationFactory) {
        this.configurationFactory = configurationFactory;
    }

    public void configTo(Configuration configuration) {
        configuration.setCacheEnabled(cacheEnabled);
        configuration.setLazyLoadingEnabled(lazyLoadingEnabled);
        configuration.setAggressiveLazyLoading(aggressiveLazyLoading);
        configuration.setMultipleResultSetsEnabled(multipleResultSetsEnabled);
        configuration.setUseColumnLabel(useColumnLabel);
        configuration.setUseGeneratedKeys(useGeneratedKeys);
        configuration.setAutoMappingBehavior(autoMappingBehavior);
        configuration.setAutoMappingUnknownColumnBehavior(autoMappingUnknownColumnBehavior);
        configuration.setDefaultExecutorType(defaultExecutorType);
        configuration.setDefaultStatementTimeout(defaultStatementTimeout);
        configuration.setDefaultFetchSize(defaultFetchSize);
        configuration.setSafeRowBoundsEnabled(safeRowBoundsEnabled);
        configuration.setSafeResultHandlerEnabled(safeResultHandlerEnabled);
        configuration.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase);
        configuration.setLocalCacheScope(localCacheScope);
        configuration.setJdbcTypeForNull(jdbcTypeForNull);
        configuration.setLazyLoadTriggerMethods(lazyLoadTriggerMethods);
        configuration.setDefaultScriptingLanguage(defaultScriptingLanguage);
        configuration.setDefaultEnumTypeHandler(defaultEnumTypeHandler);
        configuration.setCallSettersOnNulls(callSettersOnNulls);
        configuration.setReturnInstanceForEmptyRow(multipleResultSetsEnabled);
        configuration.setLogPrefix(logPrefix);
        configuration.setLogImpl(logImpl);
        configuration.setProxyFactory(proxyFactory);
        configuration.setVfsImpl(vfsImpl);
        configuration.setUseActualParamName(useActualParamName);
        configuration.setConfigurationFactory(configurationFactory);
    }
}
