/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 *    Debbie is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis;

import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mybatis.support.SqlSessionDebbieSupport;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 18:38.
 */
public class DebbieMapperFactory<Mapper> extends SqlSessionDebbieSupport implements BeanFactory<Mapper> {
    private final Class<Mapper> mapperInterface;

    private final Set<String> names = new HashSet<>();

    private final SqlSessionFactoryHandler handler;

    public DebbieMapperFactory(Class<Mapper> mapperInterface, SqlSessionFactoryHandler handler) {
        this.mapperInterface = mapperInterface;
        this.handler = handler;

        setSqlSessionFactory();

        names.add(StringUtils.toFirstCharLowerCase(mapperInterface.getSimpleName()));
        names.add(mapperInterface.getName());
    }

    private void setSqlSessionFactory() {
        SqlSessionFactory sqlSessionFactory = handler.buildSqlSessionFactory();
        setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public Set<String> getBeanNames() {
        return names;
    }

    @Override
    public Mapper factoryBean(ApplicationContext applicationContext) {
        return getSqlSession().getMapper(mapperInterface);
    }

    @Override
    public Mapper factoryNamedBean(String name, ApplicationContext applicationContext) {
        return getSqlSession().getMapper(mapperInterface);
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @Override
    public Mapper getCreatedBean() {
        return getSqlSession().getMapper(mapperInterface);
    }

    @Override
    public Class<?> getBeanClass() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!isEquals(o)) {
            return false;
        }
        if (this == o) return true;
        if (!(o instanceof DebbieMapperFactory<?> that)) return false;
        return Objects.equals(mapperInterface, that.mapperInterface) && Objects.equals(handler, that.handler);
    }

    @Override
    public int hashCode() {
        Set<String> beanNames = getBeanNames();
        // 重新计算hashcode
        int h = 0;
        for (String obj : beanNames) {
            if (obj != null) {
                h += obj.hashCode();
            }
        }
        return h + Objects.hash(mapperInterface, handler);
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        names.clear();
    }
}
