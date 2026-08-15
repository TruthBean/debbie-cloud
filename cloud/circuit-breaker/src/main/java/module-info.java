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
 * debbie-circuit-breaker module, a circuit breaker, fallback and rate limiter with truthbean debbie.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.circuitbreaker {
    requires transitive com.truthbean.debbie.core;

    exports com.truthbean.debbie.circuitbreaker;
    exports com.truthbean.debbie.circuitbreaker.fallback;
    exports com.truthbean.debbie.circuitbreaker.ratelimit;
    exports com.truthbean.debbie.circuitbreaker.event;

    opens com.truthbean.debbie.circuitbreaker to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.circuitbreaker.fallback to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.circuitbreaker.ratelimit to com.truthbean.debbie.core, com.truthbean.core;
    opens com.truthbean.debbie.circuitbreaker.event to com.truthbean.debbie.core, com.truthbean.core;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.circuitbreaker.CircuitBreakerModuleStarter;
}