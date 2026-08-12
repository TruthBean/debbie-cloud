/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.data.serialize.TextSerializable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Gson-based JSON serialization utility implementing {@link TextSerializable}.
 * <p>
 * Provides a pre-configured {@link Gson} instance with support for:
 * <ul>
 *   <li>Java 8+ time types (LocalDate, LocalDateTime, LocalTime)</li>
 *   <li>Pretty-printing toggle</li>
 *   <li>Java object &lt;--&gt; JSON string conversion</li>
 *   <li>Collection and generic type support</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GsonJsonUtils implements TextSerializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonJsonUtils.class);

    /**
     * Shared Gson instance with default configuration.
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, context) ->
                            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, context) ->
                            LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, type, context) ->
                            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                            LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, type, context) ->
                            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
            .registerTypeAdapter(LocalTime.class,
                    (JsonDeserializer<LocalTime>) (json, type, context) ->
                            LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME))
            .disableHtmlEscaping()
            .create();

    /**
     * Shared Gson instance with pretty-printing enabled.
     */
    private static final Gson GSON_PRETTY = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, context) ->
                            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, context) ->
                            LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, type, context) ->
                            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                            LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, type, context) ->
                            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
            .registerTypeAdapter(LocalTime.class,
                    (JsonDeserializer<LocalTime>) (json, type, context) ->
                            LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME))
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    /**
     * @return the shared default Gson instance
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * @return the shared pretty-printing Gson instance
     */
    public static Gson getPrettyGson() {
        return GSON_PRETTY;
    }

    // ============ serialize (Java object -> JSON string) ============

    /**
     * Serializes a Java object to a JSON string.
     *
     * @param obj the object to serialize
     * @return the JSON string, or null on failure
     */
    public static String toJson(Object obj) {
        try {
            return GSON.toJson(obj);
        } catch (Exception e) {
            LOGGER.error("Gson serialize error", e);
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
            return GSON_PRETTY.toJson(obj);
        } catch (Exception e) {
            LOGGER.error("Gson serialize error", e);
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
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            LOGGER.error("Gson deserialize error", e);
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
            return GSON.fromJson(reader, clazz);
        } catch (Exception e) {
            LOGGER.error("Gson input stream deserialize error", e);
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
     * @param <T>   the raw target type
     * @param json  the JSON string
     * @param type  the type token (e.g. {@code new TypeToken<List<String>>(){}})
     * @return the deserialized object, or null on failure
     */
    public static <T> T jsonToBean(String json, Type type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            LOGGER.error("Gson deserialize error", e);
        }
        return null;
    }

    /**
     * Deserializes a JSON string to a parameterized type using a TypeToken.
     *
     * @param <T>       the target type
     * @param json      the JSON string
     * @param typeToken the type token
     * @return the deserialized object, or null on failure
     */
    public static <T> T jsonToBean(String json, TypeToken<T> typeToken) {
        return jsonToBean(json, typeToken.getType());
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
            Type listType = TypeToken.getParameterized(List.class, elementType).getType();
            return GSON.fromJson(json, listType);
        } catch (Exception e) {
            LOGGER.error("Gson deserialize error", e);
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
            Type setType = TypeToken.getParameterized(Set.class, elementType).getType();
            return GSON.fromJson(json, setType);
        } catch (Exception e) {
            LOGGER.error("Gson deserialize error", e);
        }
        return null;
    }

    /**
     * Deserializes a JSON string to a Map of the specified key/value types.
     *
     * @param <K>       the key type
     * @param <V>       the value type
     * @param json      the JSON string
     * @param keyType   the key class
     * @param valueType the value class
     * @return the map, or null on failure
     */
    public static <K, V> Map<K, V> jsonToMap(String json, Class<K> keyType, Class<V> valueType) {
        try {
            Type mapType = TypeToken.getParameterized(Map.class, keyType, valueType).getType();
            return GSON.fromJson(json, mapType);
        } catch (Exception e) {
            LOGGER.error("Gson deserialize error", e);
        }
        return null;
    }

    // ============ collection support ============

    /**
     * Deserializes a JSON string to a Collection of the specified type.
     *
     * @param <T>            the element type
     * @param body           the JSON string
     * @param collectionType the collection class (e.g. List.class, Set.class)
     * @param clazz          the element class
     * @return the collection, or null on failure
     */
    @SuppressWarnings("rawtypes")
    public static <T> Collection<T> jsonToCollectionBean(String body, Class<? extends Collection> collectionType, Class<T> clazz) {
        try {
            Type collectionTypeToken = TypeToken.getParameterized(collectionType, clazz).getType();
            return GSON.fromJson(body, collectionTypeToken);
        } catch (Exception e) {
            LOGGER.error("Gson deserialize error", e);
        }
        return null;
    }

    /**
     * Deserializes a JSON string to a List of the specified element type.
     *
     * @param <T>   the element type
     * @param body  the JSON string
     * @param clazz the element class
     * @return the list, or null on failure
     */
    public static <T> List<T> jsonToListBean(String body, Class<T> clazz) {
        return jsonToList(body, clazz);
    }

    /**
     * Deserializes a JSON input stream to a Set of the specified element type.
     *
     * @param <T>    the element type
     * @param stream the JSON input stream
     * @param clazz  the element class
     * @return the set, or null on failure
     */
    public static <T> Set<T> jsonStreamToSetBean(InputStream stream, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            Type setType = TypeToken.getParameterized(Set.class, clazz).getType();
            return GSON.fromJson(reader, setType);
        } catch (Exception e) {
            LOGGER.error("Gson input stream deserialize error", e);
        }
        return null;
    }

    /**
     * Deserializes a JSON input stream to a List of the specified element type.
     *
     * @param <T>    the element type
     * @param stream the JSON input stream
     * @param clazz  the element class
     * @return the list, or null on failure
     */
    public static <T> List<T> jsonStreamToListBean(InputStream stream, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            return GSON.fromJson(reader, listType);
        } catch (Exception e) {
            LOGGER.error("Gson input stream deserialize error", e);
        }
        return null;
    }

    // ============ JsonElement support ============

    /**
     * Parses a JSON string to a Gson {@link JsonElement}.
     *
     * @param json the JSON string
     * @return the parsed JsonElement
     */
    public static JsonElement parse(String json) {
        return JsonParser.parseString(json);
    }

    /**
     * Converts a Java object to a Gson {@link JsonElement}.
     *
     * @param obj the object to convert
     * @return the resulting JsonElement
     */
    public static JsonElement toJsonTree(Object obj) {
        return GSON.toJsonTree(obj);
    }

    /**
     * Converts a Gson {@link JsonElement} to a Java object.
     *
     * @param <T>   the target type
     * @param json  the JsonElement
     * @param clazz the target class
     * @return the deserialized object
     */
    public static <T> T fromJsonTree(JsonElement json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}