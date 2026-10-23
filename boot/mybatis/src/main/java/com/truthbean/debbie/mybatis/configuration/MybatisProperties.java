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

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.properties.PropertiesException;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.util.*;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class MybatisProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<MybatisConfiguration> {

    public static final String ENABLE_KEY = "debbie.mybatis.enable";
    //===========================================================================
    private static final String MYBATIS_CONFIG_XML_LOCATION = "debbie.mybatis.config-xml-location";
    private static final String MYBATIS_ENVIRONMENT = "debbie.mybatis.environment";
    private static final String MYBATIS_PROPERTIES = "debbie.mybatis.properties.";
    private static final String MYBATIS_MAPPER_LOCATIONS = "debbie.mybatis.mapper-locations";
    //===========================================================================
    private static final String SETTINGS_KEY_PREFIX = "debbie.mybatis.settings.";

    private final Map<String, Map<String, MybatisConfiguration>> map = new HashMap<>();
    private final MybatisConfiguration configuration;
    private static MybatisProperties instance;

    public MybatisProperties(ApplicationContext context) {
        configuration = new MybatisConfiguration();

        configuration.setMybatisConfigXmlLocation(getValue(MYBATIS_CONFIG_XML_LOCATION));
        configuration.setEnvironment(getStringValue(MYBATIS_ENVIRONMENT, "default"));

        MyBatisConfigurationSettings settings = new MyBatisConfigurationSettings();
        loadSettings(settings);
        configuration.setSettings(settings);

        configuration.setConfigurationProperties(getMatchedKey(MYBATIS_PROPERTIES));

        List<String> list = getStringListValue(MYBATIS_MAPPER_LOCATIONS, ";");
        ResourceResolver resourceResolver = context.getResourceResolver();
        resolveMapperLocations(list, resourceResolver);
        map.computeIfAbsent(DEFAULT_PROFILE, k -> new HashMap<>()).put(DEFAULT_CATEGORY, configuration);
    }

    private void resolveMapperLocations(List<String> patternList, ResourceResolver resourceResolver) {
        List<String> list = new ArrayList<>();
        if (patternList != null && !patternList.isEmpty()) {
            for (String pattern : patternList) {
                list.addAll(resourceResolver.getMatchedResources(pattern));
            }
        }
        configuration.setMapperLocations(list);
    }

    /**
     * Read every mybatis setting from the environment using the prefix
     * {@code debbie.mybatis.settings.} and fill the settings object.
     *
     * @param settings the settings to fill
     */
    private void loadSettings(MyBatisConfigurationSettings settings) {
        settings.setCacheEnabled(getBooleanValue(SETTINGS_KEY_PREFIX + "cache-enabled", true));
        settings.setLazyLoadingEnabled(getBooleanValue(SETTINGS_KEY_PREFIX + "lazy-loading-enabled", false));
        settings.setAggressiveLazyLoading(getBooleanValue(SETTINGS_KEY_PREFIX + "aggressive-lazy-loading", false));
        settings.setMultipleResultSetsEnabled(getBooleanValue(SETTINGS_KEY_PREFIX + "multiple-result-sets-enabled", true));
        settings.setUseColumnLabel(getBooleanValue(SETTINGS_KEY_PREFIX + "use-column-label", true));
        settings.setUseGeneratedKeys(getBooleanValue(SETTINGS_KEY_PREFIX + "use-generated-keys", false));
        settings.setAutoMappingBehavior(getEnumValue(SETTINGS_KEY_PREFIX + "auto-mapping-behavior", AutoMappingBehavior.PARTIAL));
        settings.setAutoMappingUnknownColumnBehavior(getEnumValue(SETTINGS_KEY_PREFIX + "auto-mapping-unknown-column-behavior", AutoMappingUnknownColumnBehavior.NONE));
        settings.setDefaultExecutorType(getEnumValue(SETTINGS_KEY_PREFIX + "default-executor-type", ExecutorType.SIMPLE));
        settings.setDefaultStatementTimeout(getNullableInteger(SETTINGS_KEY_PREFIX + "default-statement-timeout"));
        settings.setDefaultFetchSize(getNullableInteger(SETTINGS_KEY_PREFIX + "default-fetch-size"));
        settings.setSafeRowBoundsEnabled(getBooleanValue(SETTINGS_KEY_PREFIX + "safe-row-bounds-enabled", false));
        settings.setSafeResultHandlerEnabled(getBooleanValue(SETTINGS_KEY_PREFIX + "safe-result-handler-enabled", true));
        settings.setMapUnderscoreToCamelCase(getBooleanValue(SETTINGS_KEY_PREFIX + "map-underscore-to-camel-case", false));
        settings.setLocalCacheScope(getEnumValue(SETTINGS_KEY_PREFIX + "local-cache-scope", LocalCacheScope.SESSION));
        settings.setJdbcTypeForNull(getEnumValue(SETTINGS_KEY_PREFIX + "jdbc-type-for-null", JdbcType.OTHER));
        settings.setLazyLoadTriggerMethods(getSetValue(SETTINGS_KEY_PREFIX + "lazy-load-trigger-methods", new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString"))));

        Class<?> defaultScriptingLanguage = getClassValue(SETTINGS_KEY_PREFIX + "default-scripting-language", null);
        if (defaultScriptingLanguage != null) {
            settings.setDefaultScriptingLanguage(defaultScriptingLanguage.asSubclass(LanguageDriver.class));
        }
        Class<?> defaultEnumTypeHandler = getClassValue(SETTINGS_KEY_PREFIX + "default-enum-type-handler", null);
        if (defaultEnumTypeHandler != null) {
            settings.setDefaultEnumTypeHandler(defaultEnumTypeHandler.asSubclass(TypeHandler.class));
        }

        settings.setCallSettersOnNulls(getBooleanValue(SETTINGS_KEY_PREFIX + "call-setters-on-nulls", false));
        settings.setReturnInstanceForEmptyRow(getBooleanValue(SETTINGS_KEY_PREFIX + "return-instance-for-empty-row", false));
        settings.setLogPrefix(getValue(SETTINGS_KEY_PREFIX + "log-prefix"));

        Class<?> logImpl = getClassValue(SETTINGS_KEY_PREFIX + "log-impl", null);
        if (logImpl != null) {
            settings.setLogImpl(logImpl.asSubclass(Log.class));
        }
        String proxyFactory = getValue(SETTINGS_KEY_PREFIX + "proxy-factory");
        if (proxyFactory != null && !proxyFactory.isBlank()) {
            settings.setProxyFactory((ProxyFactory) newInstance(proxyFactory.trim()));
        }
        Class<?> vfsImpl = getClassValue(SETTINGS_KEY_PREFIX + "vfs-impl", null);
        if (vfsImpl != null) {
            settings.setVfsImpl(vfsImpl.asSubclass(VFS.class));
        }

        settings.setUseActualParamName(getBooleanValue(SETTINGS_KEY_PREFIX + "use-actual-param-name", true));
        settings.setConfigurationFactory(getClassValue(SETTINGS_KEY_PREFIX + "configuration-factory", null));
    }

    private <E extends Enum<E>> E getEnumValue(String key, E defaultValue) {
        String value = getValue(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Enum.valueOf(defaultValue.getDeclaringClass(), value.trim().toUpperCase());
    }

    private Integer getNullableInteger(String key) {
        String value = getValue(key);
        if (value == null || value.isBlank()) {
            return null;
        }
        return Integer.valueOf(value.trim());
    }

    private Set<String> getSetValue(String key, Set<String> defaultValue) {
        String value = getValue(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        Set<String> result = new HashSet<>();
        for (String s : value.split(",")) {
            if (!s.isBlank()) {
                result.add(s.trim());
            }
        }
        return result;
    }

    private Object newInstance(String className) {
        try {
            return Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new PropertiesException("cannot create instance of " + className, e);
        }
    }

    public static MybatisConfiguration toConfiguration(ApplicationContext context) {
        if (instance == null) {
            instance = new MybatisProperties(context);
        }
        return instance.configuration;
    }

    public MybatisConfiguration loadConfiguration() {
        return configuration;
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    public MybatisConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        return map.getOrDefault(DEFAULT_PROFILE, new HashMap<>()).get(name);
    }

    @Override
    public Map<String, Map<String, MybatisConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return map;
    }

    @Override
    public Set<String> getCategories(String profile) {
        return map.getOrDefault(profile, new HashMap<>()).keySet();
    }

    @Override
    public boolean containConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return map.containsKey(profile) && map.get(profile).containsKey(category);
    }

    @Override
    public MybatisConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return map.getOrDefault(profile, new HashMap<>()).get(category);
    }

    @Override
    public void close() throws Exception {
        map.clear();
        instance = null;
    }
}
