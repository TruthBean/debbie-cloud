/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus.refresh;

import com.truthbean.debbie.bus.event.RefreshBusEvent;

/**
 * handler invoked when a {@link RefreshBusEvent} targets the local service.
 * <p>
 * register implementations to refresh local configuration, caches, beans, etc.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@FunctionalInterface
public interface RefreshHandler {

    void onRefresh(RefreshBusEvent event);
}