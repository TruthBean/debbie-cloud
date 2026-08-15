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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bus.event.RefreshBusEvent;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;

import java.util.Map;

/**
 * default {@link RefreshHandler} that applies the changed keys carried by a
 * {@link RefreshBusEvent} to the local {@link EnvironmentDepositoryHolder}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EnvironmentRefreshHandler implements RefreshHandler {

    private final EnvironmentDepositoryHolder environmentHolder;

    public EnvironmentRefreshHandler(EnvironmentDepositoryHolder environmentHolder) {
        this.environmentHolder = environmentHolder;
    }

    @Override
    public void onRefresh(RefreshBusEvent event) {
        if (environmentHolder == null) {
            return;
        }
        Map<String, String> keys = event.getKeys();
        if (keys == null || keys.isEmpty()) {
            LOGGER.info(() -> "bus refresh received with no keys, nothing to apply");
            return;
        }
        for (var entry : keys.entrySet()) {
            try {
                environmentHolder.addProperty(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LOGGER.error("failed to apply refreshed property " + entry.getKey(), e);
            }
        }
        LOGGER.info(() -> "bus refresh applied " + keys.size() + " keys");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentRefreshHandler.class);
}