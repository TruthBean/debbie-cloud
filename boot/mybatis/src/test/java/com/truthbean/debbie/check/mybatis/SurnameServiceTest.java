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

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@DebbieApplicationTest
class SurnameServiceTest {

    @BeforeAll
    static void before() {
        System.setProperty("redis.properties.filename", "application.properties");
    }

    @Test
    void insert(@BeanInject SurnameService surnameService) {
        var q = new Surname();
        q.setBegin(new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        q.setOrigin("");
        q.setWebsite("https://www.qu.org");
        q.setName("屈");
        var b = surnameService.insert(q);
        System.out.println(b);
        System.out.println(q);

        System.out.println("------------------------------------");

        var z = new Surname();
        z.setBegin(new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        z.setOrigin("");
        z.setWebsite("https://www.zhao.org");
        z.setName("赵");
        var bz = surnameService.insert(z);
        System.out.println(bz);
        System.out.println(z);

        /*List<Surname> surnames = surnameService.selectAll();
        System.out.println(surnames);
        System.out.println("-----------------------------------------------------");
        surnames = surnameService.selectAll();
        System.out.println(surnames);*/
    }

    @Test
    void save(@BeanInject SurnameService surnameService) throws MalformedURLException {
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
    void selectById(@BeanInject SurnameService surnameService) {
        Optional<Surname> surname = surnameService.selectById(1L);
        System.out.println(surname);
    }

    @Test
    void selectAll(@BeanInject SurnameService surnameService) {
        doSelectAll(surnameService);
    }

    void doSelectAll(SurnameService surnameService) {
        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                List<Surname> surnames = surnameService.selectAll();
                System.out.println(surnames);
                System.out.println("-----------------------------------------------------");
                surnames = surnameService.selectAll();
                System.out.println(surnames);
            }).start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void doNothing() {
    }
}