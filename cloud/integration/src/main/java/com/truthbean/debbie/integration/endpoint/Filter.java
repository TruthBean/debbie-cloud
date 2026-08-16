/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration.endpoint;

import com.truthbean.debbie.integration.Message;

import java.util.function.Predicate;

/**
 * A filter that passes or discards messages based on a predicate.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Filter implements Predicate<Message<?>> {

    private final Predicate<Object> filterPredicate;

    public Filter(Predicate<Object> filterPredicate) {
        this.filterPredicate = filterPredicate;
    }

    @Override
    public boolean test(Message<?> message) {
        return filterPredicate.test(message.getPayload());
    }
}