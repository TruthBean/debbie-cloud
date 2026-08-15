/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tracing.test;

import com.truthbean.debbie.tracing.Span;
import com.truthbean.debbie.tracing.TraceContext;
import com.truthbean.debbie.tracing.TraceIdGenerator;
import com.truthbean.debbie.tracing.Tracer;
import com.truthbean.debbie.tracing.TracingConfiguration;
import com.truthbean.debbie.tracing.TracingException;
import com.truthbean.debbie.tracing.reporter.CompositeReporter;
import com.truthbean.debbie.tracing.reporter.InMemoryReporter;
import com.truthbean.debbie.tracing.reporter.LoggingReporter;
import com.truthbean.debbie.tracing.reporter.TraceReporter;
import com.truthbean.debbie.tracing.sampler.AlwaysSampler;
import com.truthbean.debbie.tracing.sampler.NeverSampler;
import com.truthbean.debbie.tracing.sampler.ProbabilitySampler;
import com.truthbean.debbie.tracing.sampler.Sampler;
import com.truthbean.debbie.tracing.sampler.SamplerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class TracingTest {

    // ---- TraceIdGenerator tests ----

    @Test
    public void traceIdShouldBe32HexChars() {
        var traceId = TraceIdGenerator.generateTraceId();
        assertEquals(32, traceId.length());
        assertTrue(TraceIdGenerator.isValidTraceId(traceId));
    }

    @Test
    public void spanIdShouldBe16HexChars() {
        var spanId = TraceIdGenerator.generateSpanId();
        assertEquals(16, spanId.length());
        assertTrue(TraceIdGenerator.isValidSpanId(spanId));
    }

    @Test
    public void isValidTraceIdShouldRejectInvalid() {
        assertFalse(TraceIdGenerator.isValidTraceId(null));
        assertFalse(TraceIdGenerator.isValidTraceId(""));
        assertFalse(TraceIdGenerator.isValidTraceId("abc"));
        assertFalse(TraceIdGenerator.isValidTraceId("xyz1234567890xyz1234567890xyz"));
    }

    @Test
    public void isValidSpanIdShouldRejectInvalid() {
        assertFalse(TraceIdGenerator.isValidSpanId(null));
        assertFalse(TraceIdGenerator.isValidSpanId(""));
        assertFalse(TraceIdGenerator.isValidSpanId("abc"));
    }

    @Test
    public void traceIdsShouldBeUnique() {
        var id1 = TraceIdGenerator.generateTraceId();
        var id2 = TraceIdGenerator.generateTraceId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void spanIdsShouldBeUnique() {
        var id1 = TraceIdGenerator.generateSpanId();
        var id2 = TraceIdGenerator.generateSpanId();
        assertNotEquals(id1, id2);
    }

    // ---- TraceContext tests ----

    @Test
    public void contextCreateShouldGenerateIds() {
        var ctx = TraceContext.create(true);
        assertTrue(TraceIdGenerator.isValidTraceId(ctx.getTraceId()));
        assertTrue(TraceIdGenerator.isValidSpanId(ctx.getSpanId()));
        assertNull(ctx.getParentSpanId());
        assertTrue(ctx.isRoot());
        assertTrue(ctx.isSampled());
    }

    @Test
    public void contextChildOfShouldLinkParent() {
        var parent = TraceContext.create(true);
        var child = TraceContext.childOf(parent);
        assertEquals(parent.getTraceId(), child.getTraceId());
        assertEquals(parent.getSpanId(), child.getParentSpanId());
        assertNotEquals(parent.getSpanId(), child.getSpanId());
        assertFalse(child.isRoot());
    }

    @Test
    public void contextChildOfNullShouldCreateRoot() {
        var child = TraceContext.childOf(null);
        assertTrue(child.isRoot());
    }

    @Test
    public void contextWithSpanIdShouldCreateSibling() {
        var ctx = TraceContext.create(true);
        var sibling = ctx.withSpanId(TraceIdGenerator.generateSpanId());
        assertEquals(ctx.getTraceId(), sibling.getTraceId());
        assertEquals(ctx.getSpanId(), sibling.getParentSpanId());
    }

    @Test
    public void contextToStringShouldContainTraceId() {
        var ctx = TraceContext.create(true);
        var str = ctx.toString();
        assertTrue(str.contains(ctx.getTraceId()));
        assertTrue(str.contains(ctx.getSpanId()));
    }

    // ---- Span tests ----

    @Test
    public void spanShouldRecordDuration() throws InterruptedException {
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test-operation");
        Thread.sleep(10);
        span.end();
        assertTrue(span.getDurationMillis() >= 0);
        assertTrue(span.isEnded());
    }

    @Test
    public void spanShouldNotEndTwice() {
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test");
        span.end();
        var firstEnd = span.getEndMicros();
        span.end();
        assertEquals(firstEnd, span.getEndMicros());
    }

    @Test
    public void spanShouldStoreTags() {
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test");
        span.tag("http.method", "GET");
        span.tag("http.status", 200);
        span.tag("cache.hit", true);
        assertEquals("GET", span.getTag("http.method"));
        assertEquals("200", span.getTag("http.status"));
        assertEquals("true", span.getTag("cache.hit"));
    }

    @Test
    public void spanShouldRecordEvents() {
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test");
        span.event("start-processing");
        span.event("finish-processing");
        assertEquals(2, span.getEvents().size());
        assertEquals("start-processing", span.getEvents().get(0).getName());
    }

    @Test
    public void spanShouldRecordError() {
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test");
        var ex = new RuntimeException("something went wrong");
        span.error(ex);
        assertEquals(Span.STATUS_ERROR, span.getStatus());
        assertEquals("something went wrong", span.getStatusDescription());
        assertEquals("java.lang.RuntimeException", span.getTag("exception.class"));
    }

    @Test
    public void spanShouldSetStatus() {
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test");
        span.setStatus(Span.STATUS_ERROR, "timeout");
        assertEquals(Span.STATUS_ERROR, span.getStatus());
        assertEquals("timeout", span.getStatusDescription());
    }

    @Test
    public void spanToMapShouldContainAllFields() {
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test-op");
        span.tag("key", "value");
        span.end();
        var map = span.toMap();
        assertEquals(ctx.getTraceId(), map.get("traceId"));
        assertEquals(ctx.getSpanId(), map.get("spanId"));
        assertEquals("test-op", map.get("name"));
        assertEquals("OK", map.get("status"));
    }

    @Test
    public void spanToStringShouldContainName() {
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "my-operation");
        var str = span.toString();
        assertTrue(str.contains("my-operation"));
    }

    // ---- Sampler tests ----

    @Test
    public void alwaysSamplerShouldAlwaysSample() {
        var sampler = new AlwaysSampler();
        assertTrue(sampler.shouldSample("any-trace-id"));
        assertEquals("always", sampler.name());
    }

    @Test
    public void neverSamplerShouldNeverSample() {
        var sampler = new NeverSampler();
        assertFalse(sampler.shouldSample("any-trace-id"));
        assertEquals("never", sampler.name());
    }

    @Test
    public void probabilitySamplerWithRateOneShouldAlwaysSample() {
        var sampler = new ProbabilitySampler(1.0);
        assertTrue(sampler.shouldSample("trace-1"));
        assertTrue(sampler.shouldSample("trace-2"));
    }

    @Test
    public void probabilitySamplerWithRateZeroShouldNeverSample() {
        var sampler = new ProbabilitySampler(0.0);
        assertFalse(sampler.shouldSample("trace-1"));
    }

    @Test
    public void probabilitySamplerShouldClampRate() {
        var sampler = new ProbabilitySampler(2.0);
        assertEquals(1.0, sampler.getRate());
        var sampler2 = new ProbabilitySampler(-1.0);
        assertEquals(0.0, sampler2.getRate());
    }

    @Test
    public void probabilitySamplerShouldEventuallySampleAtRate() {
        var sampler = new ProbabilitySampler(0.5);
        var sampled = 0;
        for (int i = 0; i < 10000; i++) {
            if (sampler.shouldSample("trace-" + i)) sampled++;
        }
        assertTrue(sampled > 4000 && sampled < 6000);
    }

    @Test
    public void samplerFactoryShouldCreateAlways() {
        assertEquals("always", SamplerFactory.create("always").name());
    }

    @Test
    public void samplerFactoryShouldCreateNever() {
        assertEquals("never", SamplerFactory.create("never").name());
    }

    @Test
    public void samplerFactoryShouldCreateProbability() {
        assertEquals("probability", SamplerFactory.create("probability").name());
    }

    @Test
    public void samplerFactoryShouldCreateProbabilityWithRate() {
        var sampler = SamplerFactory.create("probability", 0.5);
        assertEquals("probability", sampler.name());
    }

    @Test
    public void samplerFactoryShouldDefaultToAlwaysForUnknown() {
        assertEquals("always", SamplerFactory.create("unknown").name());
    }

    @Test
    public void samplerFactoryShouldDefaultToAlwaysForNull() {
        assertEquals("always", SamplerFactory.create(null).name());
    }

    @Test
    public void samplerFactoryShouldHandleCaseInsensitive() {
        assertEquals("never", SamplerFactory.create("NEVER").name());
    }

    // ---- Reporter tests ----

    @Test
    public void inMemoryReporterShouldCollectSpans() {
        var reporter = new InMemoryReporter();
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test");
        span.end();
        reporter.report(span);
        assertEquals(1, reporter.getSpanCount());
        assertEquals(span, reporter.getSpans().get(0));
    }

    @Test
    public void inMemoryReporterShouldClear() {
        var reporter = new InMemoryReporter();
        var ctx = TraceContext.create(true);
        reporter.report(new Span(ctx, "test"));
        reporter.clear();
        assertEquals(0, reporter.getSpanCount());
    }

    @Test
    public void inMemoryReporterShouldRespectMaxSpans() {
        var reporter = new InMemoryReporter(3);
        var ctx = TraceContext.create(true);
        for (int i = 0; i < 5; i++) {
            reporter.report(new Span(ctx, "test-" + i));
        }
        assertEquals(3, reporter.getSpanCount());
    }

    @Test
    public void inMemoryReporterShouldIgnoreNull() {
        var reporter = new InMemoryReporter();
        reporter.report(null);
        assertEquals(0, reporter.getSpanCount());
    }

    @Test
    public void loggingReporterShouldNotThrow() {
        var reporter = new LoggingReporter();
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test");
        span.end();
        reporter.report(span);
        reporter.flush();
        reporter.close();
        assertEquals("logging", reporter.name());
    }

    @Test
    public void compositeReporterShouldDelegateToAll() {
        var r1 = new InMemoryReporter();
        var r2 = new InMemoryReporter();
        var composite = new CompositeReporter(r1, r2);
        var ctx = TraceContext.create(true);
        var span = new Span(ctx, "test");
        span.end();
        composite.report(span);
        assertEquals(1, r1.getSpanCount());
        assertEquals(1, r2.getSpanCount());
        assertEquals("composite", composite.name());
        assertEquals(2, composite.getReporters().size());
    }

    @Test
    public void compositeReporterShouldFlushAndCloseAll() {
        var r1 = new InMemoryReporter();
        var r2 = new InMemoryReporter();
        var composite = new CompositeReporter(r1, r2);
        composite.flush();
        composite.close();
    }

    // ---- Tracer tests ----

    @Test
    public void tracerShouldStartAndEndSpan() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("test-svc", new AlwaysSampler(), reporter);
        var span = tracer.startSpan("operation");
        assertNotNull(span);
        assertNotNull(tracer.currentSpan());
        tracer.endSpan(span);
        assertNull(tracer.currentSpan());
        assertEquals(1, reporter.getSpanCount());
        tracer.clear();
    }

    @Test
    public void tracerShouldStartChildSpan() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("test-svc", new AlwaysSampler(), reporter);
        var parent = tracer.startSpan("parent");
        var child = tracer.startChildSpan("child");
        assertEquals(parent.getContext().getTraceId(), child.getContext().getTraceId());
        assertEquals(parent.getContext().getSpanId(), child.getContext().getParentSpanId());
        tracer.endSpan(child);
        tracer.endSpan(parent);
        assertEquals(2, reporter.getSpanCount());
        tracer.clear();
    }

    @Test
    public void tracerShouldNotReportUnsampledSpans() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("test-svc", new NeverSampler(), reporter);
        var span = tracer.startSpan("operation");
        tracer.endSpan(span);
        assertEquals(0, reporter.getSpanCount());
        tracer.clear();
    }

    @Test
    public void tracerShouldTagServiceName() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("my-service", new AlwaysSampler(), reporter);
        var span = tracer.startSpan("op");
        assertEquals("my-service", span.getTag("service.name"));
        tracer.endSpan(span);
        tracer.clear();
    }

    @Test
    public void tracerWithSpanShouldAutoEnd() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("test-svc", new AlwaysSampler(), reporter);
        tracer.withSpan("auto-op", () -> {});
        assertEquals(1, reporter.getSpanCount());
        tracer.clear();
    }

    @Test
    public void tracerWithSpanShouldRecordError() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("test-svc", new AlwaysSampler(), reporter);
        assertThrows(RuntimeException.class, () ->
            tracer.withSpan("failing-op", () -> {
                throw new RuntimeException("fail");
            })
        );
        assertEquals(1, reporter.getSpanCount());
        assertEquals(Span.STATUS_ERROR, reporter.getSpans().get(0).getStatus());
        tracer.clear();
    }

    @Test
    public void tracerCurrentContextShouldReturnNullWhenNoSpan() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("test-svc", new AlwaysSampler(), reporter);
        assertNull(tracer.currentContext());
        assertNull(tracer.currentSpan());
        tracer.clear();
    }

    @Test
    public void tracerStartChildSpanWithoutParentShouldCreateRoot() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("test-svc", new AlwaysSampler(), reporter);
        var span = tracer.startChildSpan("orphan");
        assertTrue(span.getContext().isRoot());
        tracer.endSpan(span);
        tracer.clear();
    }

    @Test
    public void tracerShouldReturnProperties() {
        var reporter = new InMemoryReporter();
        var sampler = new AlwaysSampler();
        var tracer = new Tracer("svc", sampler, reporter);
        assertEquals("svc", tracer.getServiceName());
        assertSame(sampler, tracer.getSampler());
        assertSame(reporter, tracer.getReporter());
        tracer.clear();
    }

    @Test
    public void tracerClearShouldResetStack() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("test-svc", new AlwaysSampler(), reporter);
        tracer.startSpan("op");
        assertNotNull(tracer.currentSpan());
        tracer.clear();
        assertNull(tracer.currentSpan());
    }

    // ---- TracingConfiguration tests ----

    @Test
    public void configurationDefaults() {
        var config = new TracingConfiguration();
        assertTrue(config.isEnable());
        assertEquals("application", config.getServiceName());
        assertEquals("always", config.getSamplerStrategy());
        assertEquals(0.1, config.getSamplerRate());
        assertEquals("logging", config.getReporterType());
        assertEquals(100, config.getReporterBatchSize());
        assertEquals(5000, config.getReporterConnectTimeout());
        assertEquals("w3c", config.getPropagationType());
        assertFalse(config.isPropagationIncludeTags());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new TracingConfiguration();
        config.setServiceName("my-svc");
        config.setSamplerStrategy("probability");
        config.setSamplerRate(0.5);
        config.setReporterType("http");
        config.setReporterEndpoint("http://zipkin:9411/api/v2/spans");

        var copy = config.<TracingConfiguration>copy();
        assertEquals("my-svc", copy.getServiceName());
        assertEquals("probability", copy.getSamplerStrategy());
        assertEquals(0.5, copy.getSamplerRate());
        assertEquals("http", copy.getReporterType());
        assertEquals("http://zipkin:9411/api/v2/spans", copy.getReporterEndpoint());
    }

    @Test
    public void configurationShouldGetAndSetAllFields() {
        var config = new TracingConfiguration();
        config.setEnable(false);
        config.setServiceName("order-service");
        config.setSamplerStrategy("never");
        config.setSamplerRate(0.3);
        config.setReporterType("http");
        config.setReporterEndpoint("http://jaeger:14268/api/traces");
        config.setReporterBatchSize(50);
        config.setReporterConnectTimeout(3000);
        config.setPropagationType("b3");
        config.setPropagationIncludeTags(true);

        assertFalse(config.isEnable());
        assertEquals("order-service", config.getServiceName());
        assertEquals("never", config.getSamplerStrategy());
        assertEquals(0.3, config.getSamplerRate());
        assertEquals("http", config.getReporterType());
        assertEquals("http://jaeger:14268/api/traces", config.getReporterEndpoint());
        assertEquals(50, config.getReporterBatchSize());
        assertEquals(3000, config.getReporterConnectTimeout());
        assertEquals("b3", config.getPropagationType());
        assertTrue(config.isPropagationIncludeTags());
    }

    // ---- TracingException tests ----

    @Test
    public void exceptionShouldStoreMessage() {
        var ex = new TracingException("trace not found");
        assertTrue(ex.getMessage().contains("trace not found"));
    }

    @Test
    public void exceptionShouldHandleCause() {
        var cause = new RuntimeException("io error");
        var ex = new TracingException("report failed", cause);
        assertSame(cause, ex.getCause());
    }

    // ---- Integration test ----

    @Test
    public void integrationShouldTraceNestedOperations() {
        var reporter = new InMemoryReporter();
        var tracer = new Tracer("order-service", new AlwaysSampler(), reporter);

        tracer.withSpan("place-order", () -> {
            tracer.withSpan("validate-order", () -> {});
            tracer.withSpan("save-order", () -> {});
            tracer.withSpan("publish-event", () -> {});
        });

        assertEquals(4, reporter.getSpanCount());
        var spans = reporter.getSpans();
        var rootSpan = spans.get(3);
        assertTrue(rootSpan.getContext().isRoot());
        for (int i = 0; i < 3; i++) {
            assertFalse(spans.get(i).getContext().isRoot());
            assertEquals(rootSpan.getContext().getTraceId(), spans.get(i).getContext().getTraceId());
        }
        tracer.clear();
    }
}