/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis;

import com.truthbean.debbie.bean.BeanComponentInfo;
import com.truthbean.debbie.bean.BeanComponentParser;
import com.truthbean.debbie.bean.BeanType;

import java.lang.annotation.Annotation;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-18 00:36
 */
public class SingletonBeanComponentParser implements BeanComponentParser {
    @Override
    public BeanComponentInfo parse(Annotation annotation, Class<?> beanType) {
        var info = new BeanComponentInfo();
        info.setName(beanType.getName());
        info.setType(BeanType.SINGLETON);
        info.setLazy(false);

        return info;
    }
}
