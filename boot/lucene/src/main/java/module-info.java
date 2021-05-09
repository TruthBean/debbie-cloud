/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
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
 * Created on 2021-01-28 11:08
 */
module com.truthbean.debbie.lucene {
    requires java.base;
    requires transitive com.truthbean.debbie.core;
    // requires static lucene.queryparser;
    // requires static lucene.analyzers.common;
    requires static lucene.core;

    exports com.truthbean.debbie.lucene.annotation;

    provides com.truthbean.debbie.boot.DebbieModuleStarter with com.truthbean.debbie.lucene.DebbieLuceneModuleStarter;
}