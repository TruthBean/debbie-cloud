/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
module com.truthbean.debbie.hikari {
    exports com.truthbean.debbie.hikari;

    requires transitive com.truthbean.debbie.jdbc;
    requires transitive com.zaxxer.hikari;
    requires transitive com.truthbean.debbie.metrics;
    // requires transitive com.truthbean.logger.slf4j.impl;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.hikari.HikariModuleStarter;
}