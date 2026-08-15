/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/**
 * debbie-bus module, a event bus over message broker with truthbean debbie.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.bus {
    requires transitive com.truthbean.debbie.core;

    exports com.truthbean.debbie.bus;
    exports com.truthbean.debbie.bus.event;
    exports com.truthbean.debbie.bus.broker;
    exports com.truthbean.debbie.bus.identity;
    exports com.truthbean.debbie.bus.refresh;

    opens com.truthbean.debbie.bus to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.bus.event to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.bus.broker to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.bus.identity to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.bus.refresh to com.truthbean.debbie.core, com.truthbean.core;

    uses com.truthbean.debbie.bus.broker.BusMessageBrokerFactory;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.bus.BusModuleStarter;
}