/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A minimal JSON parser sufficient for parsing Consul API responses.
 * Not a general-purpose JSON library.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class SimpleJson {

    private SimpleJson() {
    }

    public static Object parse(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        var parser = new JsonParser(json.trim());
        return parser.parseValue();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object result = parse(json);
        if (result instanceof Map) {
            return (Map<String, Object>) result;
        }
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    public static List<Object> parseArray(String json) {
        Object result = parse(json);
        if (result instanceof List) {
            return (List<Object>) result;
        }
        return List.of();
    }

    public static String toJsonString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return quote((String) value);
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map) {
            return mapToJson((Map<?, ?>) value);
        }
        if (value instanceof List) {
            return listToJson((List<?>) value);
        }
        return quote(value.toString());
    }

    public static String quote(String s) {
        if (s == null) {
            return "null";
        }
        var sb = new StringBuilder(s.length() + 2);
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private static String mapToJson(Map<?, ?> map) {
        var sb = new StringBuilder("{");
        boolean first = true;
        for (var entry : map.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(quote(String.valueOf(entry.getKey())));
            sb.append(':');
            sb.append(toJsonString(entry.getValue()));
        }
        sb.append('}');
        return sb.toString();
    }

    private static String listToJson(List<?> list) {
        var sb = new StringBuilder("[");
        boolean first = true;
        for (var item : list) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(toJsonString(item));
        }
        sb.append(']');
        return sb.toString();
    }

    // ---- parser ----

    private static final class JsonParser {
        private final String json;
        private int pos;

        JsonParser(String json) {
            this.json = json;
            this.pos = 0;
        }

        Object parseValue() {
            skipWhitespace();
            if (pos >= json.length()) {
                return null;
            }
            char c = json.charAt(pos);
            return switch (c) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't', 'f' -> parseBoolean();
                case 'n' -> parseNull();
                default -> parseNumber();
            };
        }

        private Map<String, Object> parseObject() {
            pos++;
            var map = new LinkedHashMap<String, Object>();
            skipWhitespace();
            if (pos < json.length() && json.charAt(pos) == '}') {
                pos++;
                return map;
            }
            while (pos < json.length()) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                if (pos < json.length() && json.charAt(pos) == ':') {
                    pos++;
                }
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                if (pos < json.length() && json.charAt(pos) == ',') {
                    pos++;
                    continue;
                }
                if (pos < json.length() && json.charAt(pos) == '}') {
                    pos++;
                }
                break;
            }
            return map;
        }

        private List<Object> parseArray() {
            pos++;
            var list = new ArrayList<Object>();
            skipWhitespace();
            if (pos < json.length() && json.charAt(pos) == ']') {
                pos++;
                return list;
            }
            while (pos < json.length()) {
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                if (pos < json.length() && json.charAt(pos) == ',') {
                    pos++;
                    continue;
                }
                if (pos < json.length() && json.charAt(pos) == ']') {
                    pos++;
                }
                break;
            }
            return list;
        }

        private String parseString() {
            if (pos >= json.length() || json.charAt(pos) != '"') {
                return "";
            }
            pos++;
            var sb = new StringBuilder();
            while (pos < json.length()) {
                char c = json.charAt(pos);
                if (c == '"') {
                    pos++;
                    break;
                }
                if (c == '\\') {
                    pos++;
                    if (pos < json.length()) {
                        char esc = json.charAt(pos);
                        switch (esc) {
                            case '"' -> sb.append('"');
                            case '\\' -> sb.append('\\');
                            case '/' -> sb.append('/');
                            case 'n' -> sb.append('\n');
                            case 'r' -> sb.append('\r');
                            case 't' -> sb.append('\t');
                            case 'b' -> sb.append('\b');
                            case 'f' -> sb.append('\f');
                            case 'u' -> {
                                if (pos + 4 < json.length()) {
                                    String hex = json.substring(pos + 1, pos + 5);
                                    sb.append((char) Integer.parseInt(hex, 16));
                                    pos += 4;
                                }
                            }
                            default -> sb.append(esc);
                        }
                        pos++;
                    }
                } else {
                    sb.append(c);
                    pos++;
                }
            }
            return sb.toString();
        }

        private Boolean parseBoolean() {
            if (json.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            }
            if (json.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            }
            return null;
        }

        private Object parseNull() {
            if (json.startsWith("null", pos)) {
                pos += 4;
                return null;
            }
            return null;
        }

        private Number parseNumber() {
            int start = pos;
            while (pos < json.length()) {
                char c = json.charAt(pos);
                if (Character.isDigit(c) || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E') {
                    pos++;
                } else {
                    break;
                }
            }
            String num = json.substring(start, pos);
            if (num.contains(".") || num.contains("e") || num.contains("E")) {
                return Double.parseDouble(num);
            }
            try {
                return Long.parseLong(num);
            } catch (NumberFormatException e) {
                return Double.parseDouble(num);
            }
        }

        private void skipWhitespace() {
            while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
                pos++;
            }
        }
    }
}