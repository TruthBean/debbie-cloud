/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A generic message with a payload and headers.
 *
 * @param <T> the payload type
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Message<T> {

    private final T payload;
    private final Map<String, Object> headers;
    private final String id;

    public Message(T payload) {
        this(payload, new HashMap<>());
    }

    public Message(T payload, Map<String, Object> headers) {
        this.payload = payload;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.id = UUID.randomUUID().toString();
        this.headers.putIfAbsent("id", this.id);
        this.headers.putIfAbsent("timestamp", System.currentTimeMillis());
    }

    public T getPayload() {
        return payload;
    }

    public Map<String, Object> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public Object getHeader(String key) {
        return headers.get(key);
    }

    public <H> H getHeader(String key, Class<H> type) {
        var value = headers.get(key);
        return value != null ? type.cast(value) : null;
    }

    public void setHeader(String key, Object value) {
        headers.put(key, value);
    }

    public void removeHeader(String key) {
        headers.remove(key);
    }

    public String getId() {
        return id;
    }

    public static <T> Message<T> of(T payload) {
        return new Message<>(payload);
    }

    public static <T> Message<T> of(T payload, Map<String, Object> headers) {
        return new Message<>(payload, headers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message<?> message)) return false;
        return Objects.equals(payload, message.payload) && Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload, id);
    }

    @Override
    public String toString() {
        return "Message{id=" + id + ", payload=" + payload + ", headers=" + headers + "}";
    }
}