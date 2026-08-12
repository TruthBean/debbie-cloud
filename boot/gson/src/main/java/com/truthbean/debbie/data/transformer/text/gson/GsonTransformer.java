/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.text.gson;

import com.truthbean.debbie.gson.GsonJsonUtils;
import com.truthbean.transformer.DataTransformer;

/**
 * Generic Gson-based transformer that converts between Java objects and JSON strings.
 * <p>
 * Mirror of {@code com.truthbean.debbie.data.transformer.text.jackson.JsonTransformer},
 * using Gson instead of Jackson.
 *
 * @param <T> the Java type to transform
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GsonTransformer<T> implements DataTransformer<T, String> {

    private final Class<T> clazz;

    public GsonTransformer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String transform(T original) {
        return GsonJsonUtils.toJson(original);
    }

    @Override
    public T reverse(String transformer) {
        return GsonJsonUtils.jsonToBean(transformer, this.clazz);
    }
}