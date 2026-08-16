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
 * debbie-integration module, an enterprise integration patterns module with truthbean debbie.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.integration {
    requires transitive com.truthbean.debbie.core;
    requires transitive com.truthbean.debbie.mvc;

    exports com.truthbean.debbie.integration;
    exports com.truthbean.debbie.integration.channel;
    exports com.truthbean.debbie.integration.endpoint;

    opens com.truthbean.debbie.integration to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.integration.channel to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.integration.endpoint to com.truthbean.debbie.core, com.truthbean.core;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.integration.IntegrationModuleStarter;
}