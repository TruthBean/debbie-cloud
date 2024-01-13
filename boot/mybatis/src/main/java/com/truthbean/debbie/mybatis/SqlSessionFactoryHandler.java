/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.mybatis.configuration.MybatisConfiguration;
import com.truthbean.debbie.mybatis.transaction.DebbieManagedTransactionFactory;
import com.truthbean.debbie.mybatis.transaction.MybatisTransactionFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.*;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class SqlSessionFactoryHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryHandler.class);

    private final MybatisConfiguration mybatisConfiguration;

    private Configuration configuration;
    private InputStream mybatisConfigXmlInputStream;

    private final String prefix;

    public SqlSessionFactoryHandler(ApplicationContext context, MybatisConfiguration mybatisConfiguration,
                                    MybatisTransactionFactory mybatisTransactionFactory) {
        this.mybatisConfiguration = mybatisConfiguration;
        if (getMybatisConfigXmlInputStream() == null) {
            buildConfiguration(context, mybatisTransactionFactory);
        }
        prefix = mybatisTransactionFactory.name().toLowerCase();
    }

    public SqlSessionFactoryHandler(ApplicationContext context, MybatisConfiguration mybatisConfiguration) {
        this.mybatisConfiguration = mybatisConfiguration;
        if (getMybatisConfigXmlInputStream() == null) {
            buildConfiguration(context, MybatisTransactionFactory.JDBC);
        }
        prefix = "jdbc";
    }

    private SqlSessionFactory sqlSessionFactory;

    /*public void onApplicationEvent() {
        configuration.getMappedStatementNames();
    }*/

    private InputStream getMybatisConfigXmlInputStream() {
        if (mybatisConfigXmlInputStream == null) {
            String resource = mybatisConfiguration.getMybatisConfigXmlLocation();
            if (resource != null) {
                try {
                    mybatisConfigXmlInputStream = Resources.getResourceAsStream(resource);
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return mybatisConfigXmlInputStream;
    }

    private void buildSqlSessionFactoryByXml() {
        if (mybatisConfigXmlInputStream != null && sqlSessionFactory == null) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfigXmlInputStream);
        }
    }

    private DataSourceFactory getDataSourceFactoryOrInitIfNull(ApplicationContext context) {
        GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();
        DataSourceFactory dataSourceFactory = globalBeanFactory.factoryIfPresent(DataSourceFactory.class);
        if (dataSourceFactory == null) {
            /*var register = new DataSourceFactoryBeanRegister(configurationFactory, context);
            register.registerDataSourceFactory();
            context.refreshBeans();
            dataSourceFactory = beanInitialization.getRegisterBean(DataSourceFactory.class);*/
        }
        return dataSourceFactory;
    }

    private void buildConfiguration(ApplicationContext context, MybatisTransactionFactory mybatisTransactionFactory) {
        DataSourceFactory dataSourceFactory = getDataSourceFactoryOrInitIfNull(context);
        DataSource dataSource = dataSourceFactory.getDataSource();
        TransactionFactory transactionFactory;
        switch (mybatisTransactionFactory) {
            default -> transactionFactory = new DebbieManagedTransactionFactory(dataSourceFactory.getDriverName());
            case JDBC -> transactionFactory = new JdbcTransactionFactory();
            case MANAGED -> transactionFactory = new ManagedTransactionFactory();
        }
        Environment environment = new Environment(mybatisConfiguration.getEnvironment(), transactionFactory, dataSource);
        configuration = new Configuration(environment);
        mybatisConfiguration.getSettings().configTo(configuration);

        Map<String, String> configurationProperties = mybatisConfiguration.getConfigurationProperties();
        if (configurationProperties != null && !configurationProperties.isEmpty()) {
            configuration.getVariables().putAll(configurationProperties);
        }
        ObjectFactory objectFactory = mybatisConfiguration.getObjectFactory();
        if (objectFactory != null) {
            configuration.setObjectFactory(objectFactory);
        }

        ObjectWrapperFactory objectWrapperFactory = mybatisConfiguration.getObjectWrapperFactory();
        if (objectWrapperFactory != null) {
            configuration.setObjectWrapperFactory(objectWrapperFactory);
        }

        Interceptor[] plugins = mybatisConfiguration.getPlugins();
        if (plugins != null && plugins.length > 0) {
            for (Interceptor plugin : plugins) {
                configuration.addInterceptor(plugin);
            }
        }

        ResourceResolver resourceResolver = context.getResourceResolver();
        Set<Class<?>> classes = resourceResolver.getCachedClasses();

        Set<Class<?>> alias = new HashSet<>();
        Set<Class<?>> mappedTypes = new HashSet<>();
        Set<Class<?>> mappedJdbcTypes = new HashSet<>();
        Set<Class<?>> mappers = new HashSet<>();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Alias.class)) {
                alias.add(clazz);
            }
            if (clazz.isAnnotationPresent(MappedTypes.class)) {
                mappedTypes.add(clazz);
            }
            if (clazz.isAnnotationPresent(MappedJdbcTypes.class)) {
                mappedJdbcTypes.add(clazz);
            }
            if (clazz.isAnnotationPresent(Mapper.class)) {
                mappers.add(clazz);
            }
        }

        TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
        if (!alias.isEmpty()) {
            for (Class<?> typeAliasClass : alias) {
                typeAliasRegistry.registerAlias(typeAliasClass);
                LOGGER.trace("Registered type alias: '" + typeAliasClass + "'");
            }
        }


        TypeHandler<?>[] typeHandlers = mybatisConfiguration.getTypeHandlers();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (typeHandlers != null && typeHandlers.length > 0) {
            for (TypeHandler<?> typeHandler : typeHandlers) {
                typeHandlerRegistry.register(typeHandler);
            }
        }

        Set<Class<?>> mapped = new HashSet<>();
        if (!mappedTypes.isEmpty()) {
            mapped.addAll(mappedTypes);
        }

        if (!mappedJdbcTypes.isEmpty()) {
            mapped.addAll(mappedJdbcTypes);
        }
        if (!mapped.isEmpty()) {
            for (Class<?> mappedTypeClass : mapped) {
                typeHandlerRegistry.register(mappedTypeClass);
                LOGGER.trace("Registered type handlers: '" + mappedTypeClass + "'");
            }
        }

        LanguageDriver[] scriptingLanguageDrivers = mybatisConfiguration.getScriptingLanguageDrivers();
        LanguageDriverRegistry languageRegistry = configuration.getLanguageRegistry();
        if (scriptingLanguageDrivers != null && scriptingLanguageDrivers.length > 0) {
            for (LanguageDriver scriptingLanguageDriver : scriptingLanguageDrivers) {
                languageRegistry.register(scriptingLanguageDriver);
            }
        }

        Cache cache = mybatisConfiguration.getCache();
        if (cache != null) {
            configuration.addCache(cache);
        }

        if (!mappers.isEmpty()) {
            for (Class<?> mapperClass : mappers) {
                configuration.addMapper(mapperClass);
                LOGGER.trace("Registered type mapper: '" + mapperClass + "'");
                BeanInfoManager beanInfoManager = context.getBeanInfoManager();
                if (!beanInfoManager.isBeanRegistered(mapperClass)) {
                    var factory = new DebbieMapperFactory<>(mapperClass, this);
                    beanInfoManager.registerBeanInfo(factory);
                }
            }
        }

        List<String> mapperLocations = mybatisConfiguration.getMapperLocations();
        for (String mapperLocation : mapperLocations) {
            if (mapperLocation == null) {
                continue;
            }
            try {
                Enumeration<URL> resources = ClassLoaderUtils.getDefaultClassLoader().getResources(mapperLocation);
                if (resources != null) {
                    while (resources.hasMoreElements()) {
                        InputStream inputStream = resources.nextElement().openStream();
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream,
                                configuration, mapperLocation, configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
            } finally {
                ErrorContext.instance().reset();
            }
            LOGGER.trace("Parsed mapper file: '" + mapperLocation + "'");
        }
    }

    private void buildSqlSessionFactoryByJavaConfig() {
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    public SqlSessionFactory buildSqlSessionFactory() {
        buildSqlSessionFactoryByXml();
        if (sqlSessionFactory == null) {
            buildSqlSessionFactoryByJavaConfig();
        }
        return sqlSessionFactory;
    }

    public void registerMybatisConfiguration(BeanInfoManager beanInfoManager) {
        beanInfoManager.registerBeanInfo(new SimpleBeanFactory<>(configuration, true));
    }

    public String getPrefix() {
        return prefix;
    }
}
