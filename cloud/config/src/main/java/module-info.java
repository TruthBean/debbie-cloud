/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/**
 * debbie-config module, a config server/client with truthbean debbie.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.config {
    requires transitive com.truthbean.debbie.core;
    requires transitive com.truthbean.debbie.mvc;
    requires java.net.http;

    exports com.truthbean.debbie.config;
    exports com.truthbean.debbie.config.env;
    exports com.truthbean.debbie.config.repository;
    exports com.truthbean.debbie.config.encrypt;
    exports com.truthbean.debbie.config.server;
    exports com.truthbean.debbie.config.client;

    opens com.truthbean.debbie.config to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.config.env to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.config.repository to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.config.encrypt to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.config.server to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.config.client to com.truthbean.debbie.core, com.truthbean.core;

    uses com.truthbean.debbie.config.repository.ConfigRepositoryFactory;
    uses com.truthbean.debbie.config.encrypt.ConfigEncryptorFactory;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.config.ConfigModuleStarter;
}