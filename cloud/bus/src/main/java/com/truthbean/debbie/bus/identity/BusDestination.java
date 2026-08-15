/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * the destination of a bus event.
 * <p>
 * a destination is a comma separated list of service ids with optional profile suffix
 * (e.g. {@code "order-service:dev,product-service"}).
 * the special value {@code "**"} or empty means all services.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class BusDestination {

    private static final String ALL_MARKER = "**";

    private final String raw;
    private final boolean all;
    private final List<String> targets;

    public BusDestination(String raw) {
        this.raw = raw == null ? "" : raw.trim();
        if (this.raw.isEmpty() || ALL_MARKER.equals(this.raw)) {
            this.all = true;
            this.targets = Collections.emptyList();
        } else {
            this.all = false;
            var list = new ArrayList<String>();
            for (var part : this.raw.split(",")) {
                var t = part.trim();
                if (!t.isEmpty()) {
                    list.add(t);
                }
            }
            this.targets = Collections.unmodifiableList(list);
        }
    }

    public static BusDestination all() {
        return new BusDestination(ALL_MARKER);
    }

    public static BusDestination of(String raw) {
        return new BusDestination(raw);
    }

    public String getRaw() {
        return raw;
    }

    public boolean isAll() {
        return all;
    }

    public List<String> getTargets() {
        return targets;
    }

    public boolean matches(String serviceId) {
        if (all) {
            return true;
        }
        if (serviceId == null) {
            return false;
        }
        var id = serviceId.trim();
        for (var target : targets) {
            if (target.equals(id) || target.startsWith(id + ":")) {
                return true;
            }
            if (target.endsWith(":**") && id.startsWith(target.substring(0, target.length() - 2))) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(BusIdentity identity) {
        if (identity == null) {
            return all;
        }
        return matches(identity.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusDestination that)) return false;
        return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw);
    }

    @Override
    public String toString() {
        return all ? "BusDestination{**}" : "BusDestination{" + raw + "}";
    }
}