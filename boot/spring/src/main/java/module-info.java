/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-05 11:34
 */
module com.truthbean.debbie.spring {
    exports com.truthbean.debbie.spring;
    requires transitive com.truthbean.debbie.core;
    requires transitive com.truthbean.logger.jcl;
    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.context.support;

    provides com.truthbean.debbie.boot.DebbieModuleStarter with com.truthbean.debbie.spring.SpringModuleStarter;
}