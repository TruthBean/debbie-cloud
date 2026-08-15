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

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates trace IDs and span IDs as lowercase hex strings.
 * <p>
 * Trace ID: 32 hex chars (128-bit). Span ID: 16 hex chars (64-bit).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class TraceIdGenerator {

    private TraceIdGenerator() {}

    public static String generateTraceId() {
        var sb = new StringBuilder(32);
        appendHex(sb, ThreadLocalRandom.current().nextLong());
        appendHex(sb, ThreadLocalRandom.current().nextLong());
        return sb.toString();
    }

    public static String generateSpanId() {
        var sb = new StringBuilder(16);
        appendHex(sb, ThreadLocalRandom.current().nextLong());
        return sb.toString();
    }

    public static boolean isValidTraceId(String traceId) {
        return traceId != null && traceId.length() == 32 && isHex(traceId);
    }

    public static boolean isValidSpanId(String spanId) {
        return spanId != null && spanId.length() == 16 && isHex(spanId);
    }

    private static void appendHex(StringBuilder sb, long value) {
        for (int i = 0; i < 16; i++) {
            int nibble = (int) ((value >>> (60 - i * 4)) & 0xF);
            sb.append(nibble < 10 ? (char) ('0' + nibble) : (char) ('a' + nibble - 10));
        }
    }

    private static boolean isHex(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }
}