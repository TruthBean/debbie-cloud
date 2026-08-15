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

import com.truthbean.debbie.tracing.Span;

/**
 * Reporter that exports completed spans to a backend.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface TraceReporter {

    /**
     * Report a completed span.
     *
     * @param span the completed span to report
     */
    void report(Span span);

    /**
     * Flush any buffered spans.
     */
    void flush();

    /**
     * Release resources held by this reporter.
     */
    void close();

    /**
     * Returns the name of this reporter.
     *
     * @return reporter name
     */
    String name();
}