/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.mybatis;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.mybatis.SqlSessionFactoryHandler;
import com.truthbean.debbie.mybatis.configuration.MybatisConfiguration;
import com.truthbean.debbie.mybatis.configuration.MybatisProperties;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

public class MybatisTest {

    private static ApplicationContext applicationContext;

    @BeforeAll
    static void before() {
        ApplicationFactory applicationFactory = ApplicationFactory.configure(MybatisTest.class);
        applicationContext = applicationFactory.getApplicationContext();
        DataSourceFactory dataSourceFactory = applicationContext.getGlobalBeanFactory().factory("dataSourceFactory");
    }

    @Test
    public void testSqlSessionFactory() throws IOException {
        SqlSessionFactoryHandler handler = new SqlSessionFactoryHandler(applicationContext, new MybatisProperties(applicationContext).loadConfiguration());
        SqlSessionFactory sqlSessionFactory = handler.buildSqlSessionFactory();

        System.out.println(sqlSessionFactory);
    }

    @Test
    public void testSelectOneSurname() throws IOException {
        SqlSessionFactoryHandler handler = new SqlSessionFactoryHandler(applicationContext, new MybatisProperties(applicationContext).loadConfiguration());
        SqlSessionFactory sqlSessionFactory = handler.buildSqlSessionFactory();

        try (SqlSession session = sqlSessionFactory.openSession()) {
            SurnameMapper mapper = session.getMapper(SurnameMapper.class);
            Surname surname = mapper.selectOne(1L);
            System.out.println(surname);
        }
    }

    @Test
    public void testDataTimeMapper() throws IOException {
        SqlSessionFactoryHandler handler = new SqlSessionFactoryHandler(applicationContext, new MybatisProperties(applicationContext).loadConfiguration());
        SqlSessionFactory sqlSessionFactory = handler.buildSqlSessionFactory();

        try (SqlSession session = sqlSessionFactory.openSession()) {
            DateTimeMapper mapper = session.getMapper(DateTimeMapper.class);
            LocalDateTime localDateTime = mapper.now();
            System.out.println(localDateTime);
        }
    }

}
