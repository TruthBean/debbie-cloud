/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.gson.data;

import com.truthbean.debbie.data.JsonHelper;
import com.truthbean.debbie.gson.GsonJsonUtils;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Gson-based implementation of {@link JsonHelper}.
 * <p>
 * Mirror of {@code com.truthbean.debbie.jackson.data.JacksonJsonHelper},
 * using Gson instead of Jackson.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GsonJsonHelper implements JsonHelper {

    @Override
    public String toJson(Object obj) {
        return GsonJsonUtils.toJson(obj);
    }

    @Override
    public <T> T jsonToBean(String json, Class<T> type) {
        return GsonJsonUtils.jsonToBean(json, type);
    }

    @Override
    public <T> T jsonStreamToBean(InputStream jsonInputStream, Class<T> clazz) {
        return GsonJsonUtils.jsonStreamToBean(jsonInputStream, clazz);
    }

    @Override
    public <T> Collection<T> jsonToCollectionBean(String body, Class<? extends Collection> setClass, Class<T> clazz) {
        return GsonJsonUtils.jsonToCollectionBean(body, setClass, clazz);
    }

    @Override
    public <T> List<T> jsonToListBean(String body, Class<T> clazz) {
        return GsonJsonUtils.jsonToListBean(body, clazz);
    }

    @Override
    public <T> Set<T> jsonStreamToSetBean(InputStream stream, Class<T> clazz) {
        return GsonJsonUtils.jsonStreamToSetBean(stream, clazz);
    }

    @Override
    public <T> List<T> jsonStreamToListBean(InputStream stream, Class<T> clazz) {
        return GsonJsonUtils.jsonStreamToListBean(stream, clazz);
    }
}