/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/**
 * debbie-consul module, a service discovery, distributed configuration and health checking module with truthbean debbie.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.consul {
    requires transitive com.truthbean.debbie.core;
    requires transitive com.truthbean.debbie.mvc;
    requires java.net.http;

    exports com.truthbean.debbie.consul;
    exports com.truthbean.debbie.consul.discovery;
    exports com.truthbean.debbie.consul.config;
    exports com.truthbean.debbie.consul.health;
    exports com.truthbean.debbie.consul.json;

    opens com.truthbean.debbie.consul to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.consul.discovery to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.consul.config to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.consul.health to com.truthbean.debbie.core, com.truthbean.core;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.consul.ConsulModuleStarter;
}