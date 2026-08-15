/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tracing;

import com.truthbean.debbie.tracing.reporter.TraceReporter;
import com.truthbean.debbie.tracing.sampler.Sampler;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Main entry point for distributed tracing.
 * <p>
 * The {@code Tracer} creates spans, manages the current span context
 * via a thread-local stack, applies sampling, and reports completed
 * spans to the configured {@link TraceReporter}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Tracer {

    private final String serviceName;
    private final Sampler sampler;
    private final TraceReporter reporter;
    private final ThreadLocal<ConcurrentLinkedDeque<Span>> spanStack =
            ThreadLocal.withInitial(ConcurrentLinkedDeque::new);

    public Tracer(String serviceName, Sampler sampler, TraceReporter reporter) {
        this.serviceName = serviceName;
        this.sampler = sampler;
        this.reporter = reporter;
    }

    public String getServiceName() { return serviceName; }
    public Sampler getSampler() { return sampler; }
    public TraceReporter getReporter() { return reporter; }

    /**
     * Start a new root span.
     *
     * @param name span name
     * @return the new span (started)
     */
    public Span startSpan(String name) {
        var traceId = TraceIdGenerator.generateTraceId();
        var sampled = sampler.shouldSample(traceId);
        var context = new TraceContext(traceId, TraceIdGenerator.generateSpanId(), null, sampled);
        return startSpan(context, name);
    }

    /**
     * Start a child span of the current span.
     *
     * @param name span name
     * @return the new child span (started)
     */
    public Span startChildSpan(String name) {
        var current = currentSpan();
        if (current != null) {
            return startSpan(TraceContext.childOf(current.getContext()), name);
        }
        return startSpan(name);
    }

    /**
     * Start a span with an explicit context (e.g. extracted from headers).
     *
     * @param context the trace context
     * @param name    span name
     * @return the new span (started)
     */
    public Span startSpan(TraceContext context, String name) {
        var span = new Span(context, name);
        span.tag("service.name", serviceName);
        spanStack.get().push(span);
        return span;
    }

    /**
     * End the given span and report it (if sampled).
     *
     * @param span the span to end
     */
    public void endSpan(Span span) {
        if (span == null) return;
        span.end();
        spanStack.get().remove(span);
        if (span.getContext().isSampled()) {
            reporter.report(span);
        }
    }

    /**
     * End the current span and report it.
     */
    public void endCurrentSpan() {
        endSpan(currentSpan());
    }

    /**
     * Get the current span for this thread, or {@code null}.
     *
     * @return the current span or {@code null}
     */
    public Span currentSpan() {
        var stack = spanStack.get();
        var it = stack.iterator();
        Span last = null;
        while (it.hasNext()) {
            last = it.next();
        }
        return last;
    }

    /**
     * Get the current trace context, or {@code null}.
     *
     * @return the current trace context or {@code null}
     */
    public TraceContext currentContext() {
        var span = currentSpan();
        return span != null ? span.getContext() : null;
    }

    /**
     * Clear the span stack for this thread.
     */
    public void clear() {
        spanStack.get().clear();
        spanStack.remove();
    }

    /**
     * Flush and close the reporter.
     */
    public void close() {
        reporter.flush();
        reporter.close();
    }

    /**
     * Run a runnable within a span and automatically end it.
     *
     * @param name     span name
     * @param runnable the work to execute
     */
    public void withSpan(String name, Runnable runnable) {
        var span = startChildSpan(name);
        try {
            runnable.run();
        } catch (Throwable e) {
            span.error(e);
            throw e;
        } finally {
            endSpan(span);
        }
    }
}