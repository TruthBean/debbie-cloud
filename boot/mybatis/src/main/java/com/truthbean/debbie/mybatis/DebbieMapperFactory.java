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

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.mybatis.support.SqlSessionDebbieSupport;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 18:38.
 */
public class DebbieMapperFactory<Mapper> extends SqlSessionDebbieSupport implements BeanFactory<Mapper> {
    private Class<Mapper> mapperInterface;

    private SqlSessionFactoryHandler handler;

    public DebbieMapperFactory() {
    }

    public DebbieMapperFactory(Class<Mapper> mapperInterface, SqlSessionFactoryHandler handler) {
        this.mapperInterface = mapperInterface;
        this.handler = handler;

        setSqlSessionFactory();
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {

    }

    public void setHandler(SqlSessionFactoryHandler handler) {
        this.handler = handler;
    }

    private void setSqlSessionFactory() {
        SqlSessionFactory sqlSessionFactory = handler.buildSqlSessionFactory();
        setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public Mapper getBean() {
        return getSqlSession().getMapper(mapperInterface);
    }

    @Override
    public Class<Mapper> getBeanType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
