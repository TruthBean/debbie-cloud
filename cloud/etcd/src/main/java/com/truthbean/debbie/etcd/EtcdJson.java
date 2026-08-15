/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.etcd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal JSON utility for etcd module.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class EtcdJson {

    private EtcdJson() {}

    public static String toJson(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return quote((String) value);
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        if (value instanceof Map) return mapToJson((Map<?, ?>) value);
        if (value instanceof List) return listToJson((List<?>) value);
        return quote(value.toString());
    }

    public static String quote(String s) {
        if (s == null) return "null";
        var sb = new StringBuilder(s.length() + 2).append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> { if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c); }
            }
        }
        return sb.append('"').toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        if (json == null || json.isBlank()) return Map.of();
        return (Map<String, Object>) parse(json.trim());
    }

    public static Object parse(String json) {
        return new JsonParser(json).parseValue();
    }

    private static String mapToJson(Map<?, ?> map) {
        var sb = new StringBuilder("{");
        boolean first = true;
        for (var e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append(quote(String.valueOf(e.getKey()))).append(':').append(toJson(e.getValue()));
        }
        return sb.append('}').toString();
    }

    private static String listToJson(List<?> list) {
        var sb = new StringBuilder("[");
        boolean first = true;
        for (var item : list) {
            if (!first) sb.append(',');
            first = false;
            sb.append(toJson(item));
        }
        return sb.append(']').toString();
    }

    private static final class JsonParser {
        private final String json;
        private int pos;

        JsonParser(String json) { this.json = json; this.pos = 0; }

        Object parseValue() {
            skipWs();
            if (pos >= json.length()) return null;
            return switch (json.charAt(pos)) {
                case '{' -> parseObj();
                case '[' -> parseArr();
                case '"' -> parseStr();
                case 't' -> { pos += 4; yield true; }
                case 'f' -> { pos += 5; yield false; }
                case 'n' -> { pos += 4; yield null; }
                default -> parseNum();
            };
        }

        private Map<String, Object> parseObj() {
            pos++;
            var m = new LinkedHashMap<String, Object>();
            skipWs();
            if (pos < json.length() && json.charAt(pos) == '}') { pos++; return m; }
            while (pos < json.length()) {
                skipWs();
                String k = parseStr();
                skipWs();
                if (pos < json.length() && json.charAt(pos) == ':') pos++;
                m.put(k, parseValue());
                skipWs();
                if (pos < json.length() && json.charAt(pos) == ',') { pos++; continue; }
                if (pos < json.length() && json.charAt(pos) == '}') pos++;
                break;
            }
            return m;
        }

        private List<Object> parseArr() {
            pos++;
            var l = new ArrayList<Object>();
            skipWs();
            if (pos < json.length() && json.charAt(pos) == ']') { pos++; return l; }
            while (pos < json.length()) {
                l.add(parseValue());
                skipWs();
                if (pos < json.length() && json.charAt(pos) == ',') { pos++; continue; }
                if (pos < json.length() && json.charAt(pos) == ']') pos++;
                break;
            }
            return l;
        }

        private String parseStr() {
            if (pos >= json.length() || json.charAt(pos) != '"') return "";
            pos++;
            var sb = new StringBuilder();
            while (pos < json.length()) {
                char c = json.charAt(pos);
                if (c == '"') { pos++; break; }
                if (c == '\\') { pos++; if (pos < json.length()) { sb.append(json.charAt(pos)); pos++; } }
                else { sb.append(c); pos++; }
            }
            return sb.toString();
        }

        private Number parseNum() {
            int s = pos;
            while (pos < json.length() && (Character.isDigit(json.charAt(pos)) || json.charAt(pos) == '-' || json.charAt(pos) == '.')) pos++;
            String n = json.substring(s, pos);
            return n.contains(".") ? Double.parseDouble(n) : Long.parseLong(n);
        }

        private void skipWs() { while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++; }
    }
}