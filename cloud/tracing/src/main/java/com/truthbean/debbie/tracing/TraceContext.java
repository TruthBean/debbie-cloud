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

/**
 * Immutable trace context that propagates trace identity across boundaries.
 * <p>
 * Carries {@code traceId}, {@code spanId}, and optional {@code parentSpanId}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class TraceContext {

    private final String traceId;
    private final String spanId;
    private final String parentSpanId;
    private final boolean sampled;

    public TraceContext(String traceId, String spanId, String parentSpanId, boolean sampled) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.sampled = sampled;
    }

    public static TraceContext create(boolean sampled) {
        return new TraceContext(TraceIdGenerator.generateTraceId(),
                TraceIdGenerator.generateSpanId(), null, sampled);
    }

    public static TraceContext childOf(TraceContext parent) {
        if (parent == null) {
            return create(true);
        }
        return new TraceContext(parent.traceId,
                TraceIdGenerator.generateSpanId(), parent.spanId, parent.sampled);
    }

    public String getTraceId() { return traceId; }
    public String getSpanId() { return spanId; }
    public String getParentSpanId() { return parentSpanId; }
    public boolean isSampled() { return sampled; }

    public TraceContext withSpanId(String newSpanId) {
        return new TraceContext(traceId, newSpanId, spanId, sampled);
    }

    public boolean isRoot() {
        return parentSpanId == null || parentSpanId.isEmpty();
    }

    @Override
    public String toString() {
        return "TraceContext{traceId=" + traceId
                + ", spanId=" + spanId
                + (parentSpanId != null ? ", parentSpanId=" + parentSpanId : "")
                + ", sampled=" + sampled + "}";
    }
}