/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 *    Debbie is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis;

import com.truthbean.debbie.bean.BeanComponentParser;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.mybatis.annotation.*;
import com.truthbean.debbie.mybatis.configuration.MyBatisConfigurationSettings;
import com.truthbean.debbie.mybatis.configuration.MybatisProperties;
import com.truthbean.debbie.mybatis.configuration.transformer.*;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class MybatisModuleStarter implements DebbieModuleStarter {

    @Override
    public boolean enable(EnvironmentContent envContent) {
        return envContent.getBooleanValue(MybatisProperties.ENABLE_KEY, true);
    }

    @Override
    public Map<Class<? extends Annotation>, BeanComponentParser> getComponentAnnotation() {
        Map<Class<? extends Annotation>, BeanComponentParser> set = new HashMap<>();
        BeanComponentParser parser = new SingletonBeanComponentParser();
        set.put(Mapper.class, parser);
        set.put(Alias.class, parser);
        set.put(MappedJdbcTypes.class, parser);
        set.put(MappedTypes.class, parser);
        return set;
    }

    @Override
    public void registerBean(ApplicationContext context, BeanInitialization beanInitialization) {
        beanInitialization.init(MyBatisConfigurationSettings.class);

        registerTransformer(beanInitialization);

        // MethodProxyHandlerRegister methodProxyHandlerRegister = beanFactoryHandler.getMethodProxyHandlerRegister();
        // methodProxyHandlerRegister.register(JdbcTransactional.class, MybatisTransactionalHandler.class);
    }

    private void registerTransformer(BeanInitialization beanInitialization) {
        beanInitialization.registerDataTransformer(new AutoMappingBehaviorTransformer(), AutoMappingBehavior.class, String.class);
        beanInitialization.registerDataTransformer(new AutoMappingUnknownColumnBehaviorTransformer(), AutoMappingUnknownColumnBehavior.class, String.class);
        beanInitialization.registerDataTransformer(new ExecutorTypeTransformer(), ExecutorType.class, String.class);
        beanInitialization.registerDataTransformer(new JdbcTypeTransformer(), JdbcType.class, String.class);
        beanInitialization.registerDataTransformer(new LocalCacheScopeTransformer(), LocalCacheScope.class, String.class);
    }

    @Override
    public void configure(DebbieConfigurationCenter configurationFactory, ApplicationContext context) {
        MappedBeanRegister register = new MappedBeanRegister(configurationFactory, context);
        register.registerMapper();
        register.registerSqlSessionFactory();
    }

    @Override
    public int getOrder() {
        return 51;
    }

}
