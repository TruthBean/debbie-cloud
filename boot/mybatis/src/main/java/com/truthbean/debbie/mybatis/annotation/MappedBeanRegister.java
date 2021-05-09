/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis.annotation;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.DebbieClassBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactoryBeanRegister;
import com.truthbean.debbie.mybatis.DebbieMapperFactory;
import com.truthbean.debbie.mybatis.SqlSessionFactoryHandler;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 18:27.
 */
public class MappedBeanRegister extends DataSourceFactoryBeanRegister {

    private final SqlSessionFactoryHandler sqlSessionFactoryHandler;
    private final BeanInitialization beanInitialization;
    private final ApplicationContext context;

    public MappedBeanRegister(DebbieConfigurationCenter configurationFactory, ApplicationContext context) {
        super(configurationFactory, context);
        this.context = context;
        sqlSessionFactoryHandler = new SqlSessionFactoryHandler(configurationFactory, context);
        beanInitialization = context.getBeanInitialization();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void registerMapper() {
        Set<DebbieClassBeanInfo<?>> annotatedClass = beanInitialization.getAnnotatedClass(Mapper.class);
        if (annotatedClass != null && !annotatedClass.isEmpty()) {
            for (DebbieClassBeanInfo<?> mapperBean : annotatedClass) {
                DebbieMapperFactory mapperFactory = new DebbieMapperFactory<>(mapperBean.getBeanClass(),
                        sqlSessionFactoryHandler);
                mapperFactory.setGlobalBeanFactory(context.getGlobalBeanFactory());
                mapperBean.setBeanFactory(mapperFactory);
                beanInitialization.refreshBean(mapperBean);
                context.refreshBeans();
            }
        }
    }

    public void registerSqlSessionFactory() {
        registerSingletonBean(sqlSessionFactoryHandler.buildSqlSessionFactory(), SqlSessionFactory.class, "sqlSessionFactory");
    }
}
