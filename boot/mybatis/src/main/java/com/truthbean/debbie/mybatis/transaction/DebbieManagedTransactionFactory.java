/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis.transaction;

import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates {@code MybatisTransactionInfo} instances
 *
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieManagedTransactionFactory implements TransactionFactory {

    private final DataSourceDriverName driverName;

    public DebbieManagedTransactionFactory(DataSourceDriverName driverName) {
        this.driverName = driverName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        LOGGER.debug("create MybatisTransactionInfo by dataSource ({}) with transactionIsolationLevel ({}) and autoCommit ({}) ",
                dataSource, level, autoCommit);
        MybatisTransactionInfo transactionInfo = new MybatisTransactionInfo();
        try {
            Connection connection = dataSource.getConnection();
            LOGGER.debug("connection ({}) created by dataSource", connection);
            transactionInfo.setConnection(connection);
            transactionInfo.setDriverName(driverName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (level != null) {
            transactionInfo.setTransactionIsolation(level.getLevel());
        }
        transactionInfo.setAutoCommit(autoCommit);
        TransactionManager.offer(transactionInfo);
        return transactionInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction newTransaction(Connection connection) {
        LOGGER.debug("create MybatisTransactionInfo by connection {} ", connection);
        MybatisTransactionInfo transactionInfo = new MybatisTransactionInfo();
        transactionInfo.setConnection(connection);
        transactionInfo.setDriverName(driverName);
        TransactionManager.offer(transactionInfo);
        return transactionInfo;
        // throw new UnsupportedOperationException("New Debbie transactions require a DataSource");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Properties props) {
        // not needed in this version
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieManagedTransactionFactory.class);

}