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
 * debbie-eureka module, a service registry and discovery module with truthbean debbie.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.eureka {
    requires transitive com.truthbean.debbie.core;
    requires transitive com.truthbean.debbie.mvc;
    requires java.net.http;

    exports com.truthbean.debbie.eureka;
    exports com.truthbean.debbie.eureka.model;
    exports com.truthbean.debbie.eureka.server;
    exports com.truthbean.debbie.eureka.client;

    opens com.truthbean.debbie.eureka to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.eureka.model to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.eureka.server to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.eureka.client to com.truthbean.debbie.core, com.truthbean.core;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.eureka.EurekaModuleStarter;
}