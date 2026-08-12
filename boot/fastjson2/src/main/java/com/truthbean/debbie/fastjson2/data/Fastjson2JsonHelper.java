/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.fastjson2.data;

import com.truthbean.debbie.data.JsonHelper;
import com.truthbean.debbie.fastjson2.Fastjson2JsonUtils;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Fastjson2-based implementation of {@link JsonHelper}.
 * <p>
 * Mirror of {@code com.truthbean.debbie.jackson.data.JacksonJsonHelper},
 * using Fastjson2 instead of Jackson.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Fastjson2JsonHelper implements JsonHelper {

    @Override
    public String toJson(Object obj) {
        return Fastjson2JsonUtils.toJson(obj);
    }

    @Override
    public <T> T jsonToBean(String json, Class<T> type) {
        return Fastjson2JsonUtils.jsonToBean(json, type);
    }

    @Override
    public <T> T jsonStreamToBean(InputStream jsonInputStream, Class<T> clazz) {
        return Fastjson2JsonUtils.jsonStreamToBean(jsonInputStream, clazz);
    }

    @Override
    public <T> Collection<T> jsonToCollectionBean(String body, Class<? extends Collection> setClass, Class<T> clazz) {
        return Fastjson2JsonUtils.jsonToCollectionBean(body, setClass, clazz);
    }

    @Override
    public <T> List<T> jsonToListBean(String body, Class<T> clazz) {
        return Fastjson2JsonUtils.jsonToListBean(body, clazz);
    }

    @Override
    public <T> Set<T> jsonStreamToSetBean(InputStream stream, Class<T> clazz) {
        return Fastjson2JsonUtils.jsonStreamToSetBean(stream, clazz);
    }

    @Override
    public <T> List<T> jsonStreamToListBean(InputStream stream, Class<T> clazz) {
        return Fastjson2JsonUtils.jsonStreamToListBean(stream, clazz);
    }
}