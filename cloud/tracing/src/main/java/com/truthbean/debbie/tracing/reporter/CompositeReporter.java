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

import java.util.List;

/**
 * Composite reporter that delegates to multiple reporters.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class CompositeReporter implements TraceReporter {

    private final List<TraceReporter> reporters;

    public CompositeReporter(TraceReporter... reporters) {
        this.reporters = List.of(reporters);
    }

    public CompositeReporter(List<TraceReporter> reporters) {
        this.reporters = List.copyOf(reporters);
    }

    @Override
    public void report(Span span) {
        for (var r : reporters) {
            r.report(span);
        }
    }

    @Override
    public void flush() {
        for (var r : reporters) {
            r.flush();
        }
    }

    @Override
    public void close() {
        for (var r : reporters) {
            r.close();
        }
    }

    @Override
    public String name() {
        return "composite";
    }

    public List<TraceReporter> getReporters() {
        return reporters;
    }
}