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

import java.util.Objects;

/**
 * the identity of a service instance on the bus.
 * <p>
 * it identifies who publishes a bus event and who should receive it.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class BusIdentity {

    private final String id;

    public BusIdentity(String id) {
        this.id = id == null || id.isBlank() ? "default" : id;
    }

    public String getId() {
        return id;
    }

    public boolean matches(BusDestination destination) {
        if (destination == null || destination.isAll()) {
            return true;
        }
        return destination.matches(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusIdentity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BusIdentity{" + id + "}";
    }
}