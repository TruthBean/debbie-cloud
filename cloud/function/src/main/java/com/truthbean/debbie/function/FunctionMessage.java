/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.function;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Message envelope that carries a payload and headers between functions.
 *
 * @param <T> the payload type
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FunctionMessage<T> {

    public static final String FUNCTION_NAME_HEADER = "function.name";

    private final T payload;
    private final Map<String, Object> headers;

    public FunctionMessage(T payload) {
        this(payload, new LinkedHashMap<>());
    }

    public FunctionMessage(T payload, Map<String, Object> headers) {
        this.payload = payload;
        this.headers = headers != null ? new LinkedHashMap<>(headers) : new LinkedHashMap<>();
    }

    public T getPayload() {
        return payload;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Object getHeader(String key) {
        return headers.get(key);
    }

    public String getHeaderAsString(String key) {
        var v = headers.get(key);
        return v != null ? String.valueOf(v) : null;
    }

    public FunctionMessage<T> addHeader(String key, Object value) {
        headers.put(key, value);
        return this;
    }

    public String getFunctionName() {
        return getHeaderAsString(FUNCTION_NAME_HEADER);
    }

    public FunctionMessage<T> setFunctionName(String name) {
        headers.put(FUNCTION_NAME_HEADER, name);
        return this;
    }

    public <R> FunctionMessage<R> withPayload(R newPayload) {
        var msg = new FunctionMessage<>(newPayload, headers);
        return msg;
    }

    public static <T> FunctionMessage<T> of(T payload) {
        return new FunctionMessage<>(payload);
    }

    public static <T> FunctionMessage<T> of(T payload, String functionName) {
        return new FunctionMessage<>(payload).setFunctionName(functionName);
    }

    @Override
    public String toString() {
        return "FunctionMessage{payload=" + payload + ", headers=" + headers + '}';
    }
}