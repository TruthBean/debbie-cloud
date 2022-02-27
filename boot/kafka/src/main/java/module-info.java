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
 * @since 0.5.0
 * Created on 2021-01-28 11:05
 */
module com.truthbean.debbie.kafka {
    requires java.base;
    requires java.management;
    requires transitive com.truthbean.debbie.core;
    requires static kafka.clients;
    // requires com.github.luben.zstd_jni;
    // requires org.lz4.java;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.annotation;

    exports com.truthbean.debbie.kafka;

    opens com.truthbean.debbie.kafka to com.truthbean.debbie.core, com.truthbean.common.mini;

    provides com.truthbean.debbie.boot.DebbieModuleStarter with com.truthbean.debbie.kafka.KafkaModuleStarter;
}