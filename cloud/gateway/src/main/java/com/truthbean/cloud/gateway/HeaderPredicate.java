/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.cloud.gateway;

import com.truthbean.debbie.mvc.request.RouterRequest;

import java.util.regex.Pattern;

/**
 * Header-based predicate. Matches when a header value matches the given regex.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class HeaderPredicate implements GatewayPredicate {

    private final String headerName;
    private final Pattern pattern;

    public HeaderPredicate(String headerName, String regex) {
        this.headerName = headerName;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean test(RouterRequest request) {
        var httpHeader = request.getHeader();
        if (httpHeader == null) {
            return false;
        }
        String value = httpHeader.getHeader(headerName);
        return value != null && pattern.matcher(value).matches();
    }

    @Override
    public String toString() {
        return "HeaderPredicate{header=" + headerName + ", pattern=" + pattern + '}';
    }
}