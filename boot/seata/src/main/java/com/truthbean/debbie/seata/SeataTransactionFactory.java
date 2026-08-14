/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.seata;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import io.seata.core.context.RootContext;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SeataTransactionFactory {

    private final SeataConfiguration configuration;

    public SeataTransactionFactory(SeataConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Get the current XID.
     */
    public String getXid() {
        return RootContext.getXID();
    }

    /**
     * Bind a XID to current thread.
     */
    public void bind(String xid) {
        RootContext.bind(xid);
    }

    /**
     * Unbind the XID from current thread.
     */
    public void unbind() {
        RootContext.unbind();
    }

    /**
     * Create a new GlobalTransaction.
     */
    public GlobalTransaction createGlobalTransaction() throws Exception {
        return GlobalTransactionContext.getCurrentOrCreate();
    }

    public SeataConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        LOGGER.info("Seata transaction factory closed");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SeataTransactionFactory.class);
}