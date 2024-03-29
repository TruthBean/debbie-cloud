/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.freemarker.test;

import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationFactory;

@DebbieBootApplication
public class FreemarkerTest {

    public static void main(String[] args) {
        ApplicationFactory.newEmpty()
                .preInit(args)
                .init(FreemarkerTest.class.getClassLoader())
                .config()
                .create()
                .postCreate()
                .build()
                .factory()
                .start();
    }
}
