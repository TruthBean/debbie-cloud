/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.data.serialize.TextSerializable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Fastjson2-based JSON serialization utility implementing {@link TextSerializable}.
 * <p>
 * Provides access to Fastjson2's JSON API with support for:
 * <ul>
 *   <li>Java 8+ time types (built-in fastjson2 support)</li>
 *   <li>Pretty-printing toggle</li>
 *   <li>Java object &lt;--&gt; JSON string conversion</li>
 *   <li>Collection and generic type support</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Fastjson2JsonUtils implements TextSerializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Fastjson2JsonUtils.class);

    // ============ serialize (Java object -> JSON string) ============

    /**
     * Serializes a Java object to a JSON string.
     *
     * @param obj the object to serialize
     * @return the JSON string, or null on failure
     */
    public static String toJson(Object obj) {
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            LOGGER.error("Fastjson2 serialize error", e);
        }
        return null;
    }

    /**
     * Serializes a Java object to a pretty-printed JSON string.
     *
     * @param obj the object to serialize
     * @return the formatted JSON string, or null on failure
     */
    public static String toJsonPretty(Object obj) {
        try {
            return JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);
        } catch (Exception e) {
            LOGGER.error("Fastjson2 serialize error", e);
        }
        return null;
    }

    @Override
    public String serialize(Object obj) {
        return toJson(obj);
    }

    // ============ deserialize (JSON string -> Java object) ============

    /**
     * Deserializes a JSON string to a Java object of the specified type.
     *
     * @param <T>   the target type
     * @param json  the JSON string
     * @param clazz the target class
     * @return the deserialized object, or null on failure
     */
    public static <T> T jsonToBean(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            LOGGER.error("Fastjson2 deserialize error", e);
        }
        return null;
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        return jsonToBean(json, type);
    }

    /**
     * Deserializes a JSON input stream to a Java object.
     *
     * @param <T>          the target type
     * @param inputStream  the JSON input stream
     * @param clazz        the target class
     * @return the deserialized object, or null on failure
     */
    public static <T> T jsonStreamToBean(InputStream inputStream, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return JSON.parseObject(reader, clazz);
        } catch (Exception e) {
            LOGGER.error("Fastjson2 input stream deserialize error", e);
        }
        return null;
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) {
        return jsonStreamToBean(inputStream, type);
    }

    // ============ generic type support ============

    /**
     * Deserializes a JSON string to a parameterized type (e.g. List&lt;String&gt;).
     *
     * @param <T>   the target type
     * @param json  the JSON string
     * @param type  the parameterized type
     * @return the deserialized object, or null on failure
     */
    public static <T> T jsonToBean(String json, Type type) {
        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            LOGGER.error("Fastjson2 deserialize error", e);
        }
        return null;
    }

    /**
     * Deserializes a JSON string to a List of the specified element type.
     *
     * @param <T>         the element type
     * @param json        the JSON string
     * @param elementType the element class
     * @return the list, or null on failure
     */
    public static <T> List<T> jsonToList(String json, Class<T> elementType) {
        try {
            return JSON.parseArray(json, elementType);
        } catch (Exception e) {
            LOGGER.error("Fastjson2 deserialize error", e);
        }
        return null;
    }

    /**
     * Deserializes a JSON string to a Set of the specified element type.
     *
     * @param <T>         the element type
     * @param json        the JSON string
     * @param elementType the element class
     * @return the set, or null on failure
     */
    public static <T> Set<T> jsonToSet(String json, Class<T> elementType) {
        try {
            List<T> list = JSON.parseArray(json, elementType);
            return list != null ? new LinkedHashSet<>(list) : null;
        } catch (Exception e) {
            LOGGER.error("Fastjson2 deserialize error", e);
        }
        return null;
    }

    // ============ collection support (JsonHelper interface) ============

    @SuppressWarnings("rawtypes")
    public static <T> Collection<T> jsonToCollectionBean(String body, Class<? extends Collection> collectionType, Class<T> clazz) {
        try {
            List<T> list = JSON.parseArray(body, clazz);
            if (list == null) return null;
            if (collectionType == Set.class) {
                return new LinkedHashSet<>(list);
            }
            return list;
        } catch (Exception e) {
            LOGGER.error("Fastjson2 deserialize error", e);
        }
        return null;
    }

    public static <T> List<T> jsonToListBean(String body, Class<T> clazz) {
        return jsonToList(body, clazz);
    }

    public static <T> Set<T> jsonStreamToSetBean(InputStream stream, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            List<T> list = JSON.parseArray(reader, clazz);
            return list != null ? new LinkedHashSet<>(list) : null;
        } catch (Exception e) {
            LOGGER.error("Fastjson2 input stream deserialize error", e);
        }
        return null;
    }

    public static <T> List<T> jsonStreamToListBean(InputStream stream, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JSON.parseArray(reader, clazz);
        } catch (Exception e) {
            LOGGER.error("Fastjson2 input stream deserialize error", e);
        }
        return null;
    }

    // ============ JSONObject / JSONArray support ============

    /**
     * Parses a JSON string to a {@link JSONObject}.
     *
     * @param json the JSON string
     * @return the parsed JSONObject
     */
    public static JSONObject parseObject(String json) {
        return JSON.parseObject(json);
    }

    /**
     * Parses a JSON string to a {@link JSONArray}.
     *
     * @param json the JSON string
     * @return the parsed JSONArray
     */
    public static JSONArray parseArray(String json) {
        return JSON.parseArray(json);
    }

    /**
     * Converts a Java object to a {@link JSONObject}.
     *
     * @param obj the object to convert
     * @return the resulting JSONObject
     */
    public static JSONObject toJsonObject(Object obj) {
        return (JSONObject) JSON.toJSON(obj);
    }

    /**
     * Converts a {@link JSONObject} to a Java object.
     *
     * @param <T>        the target type
     * @param jsonObject the JSONObject
     * @param clazz      the target class
     * @return the deserialized object
     */
    public static <T> T toJavaObject(JSONObject jsonObject, Class<T> clazz) {
        return jsonObject.toJavaObject(clazz);
    }
}