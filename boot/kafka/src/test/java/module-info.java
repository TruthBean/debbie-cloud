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
 * Created on 2021-03-24 18:03
 */
open module com.truthbean.debbie.kafka.test {
    requires com.truthbean.debbie.kafka;
    requires com.truthbean.debbie.test;

    requires java.base;
    requires transitive com.truthbean.debbie.core;
    requires static kafka.clients;
    requires com.github.luben.zstd_jni;
    requires org.lz4.java;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.annotation;
    requires com.truthbean.logger.stdout.boot;
    requires com.truthbean.debbie.jackson;
}