/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.mybatis;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-03-23 17:13
 */
@DebbieApplicationTest
public class SystemUserMapperTest {

    @BeanInject
    private SystemUserMapper systemUserMapper;

    @BeforeEach
    public void before(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        TransactionManager.offer(factory.getTransaction());
    }

    @Test
    void selectAll() {
        List<SystemUser> systemUsers = systemUserMapper.selectAll();
        System.out.println(systemUsers);
    }
}