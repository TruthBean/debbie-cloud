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

import java.util.ArrayList;
import java.util.List;

/**
 * Reporter that collects spans in memory. Useful for testing.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class InMemoryReporter implements TraceReporter {

    private final List<Span> spans = new ArrayList<>();
    private final int maxSpans;

    public InMemoryReporter() {
        this(10000);
    }

    public InMemoryReporter(int maxSpans) {
        this.maxSpans = maxSpans;
    }

    @Override
    public void report(Span span) {
        if (span == null) return;
        synchronized (spans) {
            if (spans.size() < maxSpans) {
                spans.add(span);
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public String name() {
        return "in-memory";
    }

    public List<Span> getSpans() {
        synchronized (spans) {
            return new ArrayList<>(spans);
        }
    }

    public int getSpanCount() {
        synchronized (spans) {
            return spans.size();
        }
    }

    public void clear() {
        synchronized (spans) {
            spans.clear();
        }
    }
}