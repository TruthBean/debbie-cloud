/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tracing.reporter;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.tracing.Span;

/**
 * Reporter that logs completed spans using the debbie logger.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LoggingReporter implements TraceReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingReporter.class);

    @Override
    public void report(Span span) {
        if (span == null) return;
        LOGGER.info(() -> "[trace] " + span);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public String name() {
        return "logging";
    }
}