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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a span in a distributed trace.
 * <p>
 * A span has a name, start/end timestamps, tags, events (log annotations),
 * and a status. It belongs to exactly one {@link TraceContext}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Span {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_ERROR = "ERROR";

    private final TraceContext context;
    private final String name;
    private final long startMicros;
    private long endMicros;
    private String status = STATUS_OK;
    private String statusDescription;
    private final Map<String, String> tags = new LinkedHashMap<>();
    private final List<SpanEvent> events = new ArrayList<>();
    private volatile boolean ended;

    public Span(TraceContext context, String name) {
        this.context = context;
        this.name = name;
        this.startMicros = currentMicros();
        this.ended = false;
    }

    public Span(TraceContext context, String name, long startMicros) {
        this.context = context;
        this.name = name;
        this.startMicros = startMicros;
        this.ended = false;
    }

    public TraceContext getContext() { return context; }
    public String getName() { return name; }
    public long getStartMicros() { return startMicros; }
    public long getEndMicros() { return endMicros; }
    public String getStatus() { return status; }
    public String getStatusDescription() { return statusDescription; }
    public boolean isEnded() { return ended; }

    public long getDurationMicros() {
        if (ended) return endMicros - startMicros;
        return currentMicros() - startMicros;
    }

    public long getDurationMillis() {
        return getDurationMicros() / 1000;
    }

    public Span tag(String key, String value) {
        tags.put(key, value);
        return this;
    }

    public Span tag(String key, long value) {
        tags.put(key, String.valueOf(value));
        return this;
    }

    public Span tag(String key, boolean value) {
        tags.put(key, String.valueOf(value));
        return this;
    }

    public String getTag(String key) {
        return tags.get(key);
    }

    public Map<String, String> getTags() { return tags; }

    public Span event(String name) {
        events.add(new SpanEvent(name, currentMicros()));
        return this;
    }

    public Span event(String name, long timestampMicros) {
        events.add(new SpanEvent(name, timestampMicros));
        return this;
    }

    public List<SpanEvent> getEvents() { return events; }

    public Span setStatus(String status) {
        this.status = status;
        return this;
    }

    public Span setStatus(String status, String description) {
        this.status = status;
        this.statusDescription = description;
        return this;
    }

    public Span error(Throwable throwable) {
        this.status = STATUS_ERROR;
        this.statusDescription = throwable.getMessage();
        tags.put("exception.class", throwable.getClass().getName());
        if (throwable.getMessage() != null) {
            tags.put("exception.message", throwable.getMessage());
        }
        return this;
    }

    public Span end() {
        if (ended) return this;
        ended = true;
        endMicros = currentMicros();
        return this;
    }

    public Span end(long endMicros) {
        if (ended) return this;
        ended = true;
        this.endMicros = endMicros;
        return this;
    }

    private static long currentMicros() {
        return System.currentTimeMillis() * 1000;
    }

    public Map<String, Object> toMap() {
        var m = new LinkedHashMap<String, Object>();
        m.put("traceId", context.getTraceId());
        m.put("spanId", context.getSpanId());
        if (context.getParentSpanId() != null) {
            m.put("parentSpanId", context.getParentSpanId());
        }
        m.put("name", name);
        m.put("startMicros", startMicros);
        m.put("endMicros", endMicros);
        m.put("durationMicros", getDurationMicros());
        m.put("status", status);
        if (statusDescription != null) m.put("statusDescription", statusDescription);
        if (!tags.isEmpty()) m.put("tags", tags);
        if (!events.isEmpty()) {
            var eventList = new ArrayList<Map<String, Object>>();
            for (var e : events) eventList.add(e.toMap());
            m.put("events", eventList);
        }
        m.put("sampled", context.isSampled());
        return m;
    }

    @Override
    public String toString() {
        return "Span{name='" + name + "', traceId=" + context.getTraceId()
                + ", spanId=" + context.getSpanId()
                + ", duration=" + getDurationMillis() + "ms"
                + ", status=" + status + "}";
    }

    public static final class SpanEvent {
        private final String name;
        private final long timestampMicros;

        public SpanEvent(String name, long timestampMicros) {
            this.name = name;
            this.timestampMicros = timestampMicros;
        }

        public String getName() { return name; }
        public long getTimestampMicros() { return timestampMicros; }

        public Map<String, Object> toMap() {
            var m = new LinkedHashMap<String, Object>();
            m.put("name", name);
            m.put("timestampMicros", timestampMicros);
            return m;
        }
    }
}