/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.text.fastjson2;

import com.alibaba.fastjson2.JSONObject;

/**
 * Fastjson2 {@link JSONObject} transformer.
 * <p>
 * Mirror of {@code com.truthbean.debbie.data.transformer.text.jackson.JsonNodeTransformer},
 * using Fastjson2's {@link JSONObject} instead of Jackson's {@link com.fasterxml.jackson.databind.JsonNode}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Fastjson2JsonObjectTransformer extends Fastjson2Transformer<JSONObject> {

    public Fastjson2JsonObjectTransformer() {
        super(JSONObject.class);
    }
}