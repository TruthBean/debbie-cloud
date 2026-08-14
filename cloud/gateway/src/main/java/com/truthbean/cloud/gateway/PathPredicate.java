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
 * Path-based predicate. Supports Ant-style patterns (e.g., /api/**).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class PathPredicate implements GatewayPredicate {

    private final Pattern pattern;

    public PathPredicate(String pathPattern) {
        // Convert Ant-style path to regex
        String regex = convertPathToRegex(pathPattern);
        this.pattern = Pattern.compile(regex);
    }

    private static String convertPathToRegex(String antPath) {
        String regex = antPath
                .replace(".", "\\.")
                .replace("**", "___DOUBLE_STAR___")
                .replace("*", "[^/]*")
                .replace("___DOUBLE_STAR___", ".*");
        if (!regex.startsWith("^")) {
            regex = "^" + regex;
        }
        if (!regex.endsWith("$")) {
            regex = regex + "$";
        }
        return regex;
    }

    @Override
    public boolean test(RouterRequest request) {
        String path = request.getUrl();
        return path != null && pattern.matcher(path).matches();
    }

    @Override
    public String toString() {
        return "PathPredicate{pattern=" + pattern + '}';
    }
}