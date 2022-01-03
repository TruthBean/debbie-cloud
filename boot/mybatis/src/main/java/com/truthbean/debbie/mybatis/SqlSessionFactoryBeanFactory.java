package com.truthbean.debbie.mybatis;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mybatis.support.SqlSessionDebbieSupport;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/25 20:23.
 */
public class SqlSessionFactoryBeanFactory extends SqlSessionDebbieSupport implements BeanFactory<SqlSessionFactory> {

    private final SqlSessionFactoryHandler handler;

    private final Set<String> names = new HashSet<>();

    public SqlSessionFactoryBeanFactory(SqlSessionFactoryHandler handler) {
        this.handler = handler;

        setSqlSessionFactory();

        names.add("org.apache.ibatis.session.SqlSessionFactory");
        names.add("sqlSessionFactory");
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
    public SqlSessionFactory factoryBean(ApplicationContext applicationContext) {
        setSqlSessionFactory();
        return getSqlSessionFactory();
    }

    @Override
    public SqlSessionFactory factoryNamedBean(String name, ApplicationContext applicationContext) {
        setSqlSessionFactory();
        return getSqlSessionFactory();
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @Override
    public SqlSessionFactory getCreatedBean() {
        setSqlSessionFactory();
        return getSqlSessionFactory();
    }

    @Override
    public Class<?> getBeanClass() {
        return SqlSessionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return isEquals(o);
    }

    @Override
    public int hashCode() {
        return getHashCode(0);
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        names.clear();
    }
}
