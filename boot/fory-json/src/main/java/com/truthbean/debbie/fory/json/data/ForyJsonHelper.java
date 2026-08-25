package com.truthbean.debbie.fory.json.data;

import com.truthbean.debbie.data.JsonHelper;
import org.apache.fory.json.ForyJson;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ForyJsonHelper implements JsonHelper {
    private final ForyJson foryJson;
    public ForyJsonHelper() {
        this.foryJson = ForyJson.builder().build();
    }

    @Override
    public String toJson(Object obj) {
        return foryJson.toJson(obj);
    }

    @Override
    public <T> T jsonToBean(String json, Class<T> type) {
        return foryJson.fromJson(json, type);
    }

    @Override
    public <T> T jsonStreamToBean(InputStream inputStream, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> Collection<T> jsonToCollectionBean(String body, Class<? extends Collection> setClass, Class<T> clazz) {
        return List.of();
    }

    @Override
    public <T> List<T> jsonToListBean(String body, Class<T> clazz) {
        return List.of();
    }

    @Override
    public <T> Set<T> jsonStreamToSetBean(InputStream stream, Class<T> clazz) {
        return Set.of();
    }

    @Override
    public <T> List<T> jsonStreamToListBean(InputStream stream, Class<T> clazz) {
        return List.of();
    }
}
