/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis.annotation;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.BeanRegister;
import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mybatis.DebbieMapperFactory;
import com.truthbean.debbie.mybatis.SqlSessionFactoryBeanFactory;
import com.truthbean.debbie.mybatis.SqlSessionFactoryHandler;
import com.truthbean.debbie.mybatis.configuration.MybatisConfiguration;
import com.truthbean.debbie.mybatis.transaction.MybatisTransactionFactory;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 18:27.
 */
public class MappedBeanRegister implements BeanRegister {

    private final SqlSessionFactoryHandler sqlSessionFactoryHandler;

    public MappedBeanRegister(ApplicationContext context, MybatisConfiguration mybatisConfiguration) {
        sqlSessionFactoryHandler = new SqlSessionFactoryHandler(context, mybatisConfiguration);
        BeanInfoManager beanInfoManager = context.getBeanInfoManager();
        beanInfoManager.registerBeanInfo(new SqlSessionFactoryBeanFactory(sqlSessionFactoryHandler));
        sqlSessionFactoryHandler.registerMybatisConfiguration(context.getBeanInfoManager());
    }

    @Override
    public <Bean> boolean support(ClassBeanInfo<Bean> beanInfo) {
        Class<?> beanClass = beanInfo.getBeanClass();
        return beanClass.isInterface() && support(beanInfo, Mapper.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Bean> BeanFactory<Bean> getBeanFactory(ClassBeanInfo<Bean> beanInfo) {
        return new DebbieMapperFactory<>((Class<Bean>) beanInfo.getBeanClass(), sqlSessionFactoryHandler);
    }

    @Override
    public int getOrder() {
        return 11;
    }
}
