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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Reporter that sends completed spans to a remote HTTP endpoint (e.g. Zipkin, Jaeger).
 * <p>
 * Spans are buffered and sent in batches. Uses JDK built-in {@link HttpClient}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class HttpReporter implements TraceReporter {

    private final String endpoint;
    private final HttpClient httpClient;
    private final int batchSize;
    private final ConcurrentLinkedQueue<Span> buffer = new ConcurrentLinkedQueue<>();

    public HttpReporter(String endpoint) {
        this(endpoint, 100, 5000);
    }

    public HttpReporter(String endpoint, int batchSize, int connectTimeoutMillis) {
        this.endpoint = endpoint;
        this.batchSize = batchSize;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
                .build();
    }

    @Override
    public void report(Span span) {
        if (span == null) return;
        buffer.offer(span);
        if (buffer.size() >= batchSize) {
            flush();
        }
    }

    @Override
    public void flush() {
        var batch = new ArrayList<Span>();
        Span span;
        while ((span = buffer.poll()) != null) {
            batch.add(span);
        }
        if (batch.isEmpty()) return;
        sendBatch(batch);
    }

    private void sendBatch(List<Span> batch) {
        try {
            var json = spansToJson(batch);
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofMillis(10000))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            // re-queue on failure
            buffer.addAll(batch);
        }
    }

    private String spansToJson(List<Span> batch) {
        var sb = new StringBuilder("[");
        for (int i = 0; i < batch.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(spanToJson(batch.get(i)));
        }
        return sb.append(']').toString();
    }

    private String spanToJson(Span span) {
        var m = span.toMap();
        var sb = new StringBuilder("{");
        boolean first = true;
        for (var e : m.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(e.getKey()).append("\":");
            var v = e.getValue();
            if (v instanceof String) sb.append('"').append(v).append('"');
            else if (v instanceof Number || v instanceof Boolean) sb.append(v);
            else sb.append('"').append(v).append('"');
        }
        return sb.append('}').toString();
    }

    @Override
    public void close() {
        flush();
    }

    @Override
    public String name() {
        return "http";
    }

    public String getEndpoint() { return endpoint; }
    public int getBatchSize() { return batchSize; }
    public int getBufferSize() { return buffer.size(); }
}