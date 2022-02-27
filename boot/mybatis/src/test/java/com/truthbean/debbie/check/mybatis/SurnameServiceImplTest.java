/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.mybatis;

import com.truthbean.debbie.core.ApplicationFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * @author TruthBean
 * @since
 * Created on 2019/06/02 14:35.
 */
class SurnameServiceImplTest {

    private static SurnameService surnameService;
    private static ApplicationFactory beanFactoryHandler;

    static {
        beanFactoryHandler = ApplicationFactory.configure(SurnameServiceImplTest.class);
    }

    @BeforeAll
    static void setUp() {
        surnameService = beanFactoryHandler.getApplicationContext().getGlobalBeanFactory().factory("surnameService");
    }

    @AfterAll
    static void after() {
        beanFactoryHandler.release();
    }

    @Test
    void insert() {
        var q = new Surname();
        q.setBegin(new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        q.setOrigin("");
        q.setWebsite("https://www.zhou.org");
        q.setName("周");
        var b = surnameService.insert(q);
        System.out.println(b);
        System.out.println(q);

        List<Surname> surnames = surnameService.selectAll();
        System.out.println(surnames);
        System.out.println("-----------------------------------------------------");
        surnames = surnameService.selectAll();
        System.out.println(surnames);
    }

    @Test
    void save() throws MalformedURLException {
        var q = new Surname();
        q.setId(1L);
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("1");
        q.setWebsite("https://www.zhu.org");
        q.setName("zhu");
        var b = surnameService.save(q);
        System.out.println(b);
        System.out.println(q);
    }

    @Test
    void selectById() {
        Optional<Surname> surname = surnameService.selectById(1L);
        System.out.println(surname);
    }

    @Test
    void selectAll() {
        List<Surname> surnames = surnameService.selectAll();
        System.out.println(surnames);
        System.out.println("-----------------------------------------------------");
        surnames = surnameService.selectAll();
        System.out.println(surnames);
    }

    @Test
    void doNothing() {
    }
}