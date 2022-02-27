/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis;

import com.truthbean.debbie.jdbc.transaction.ResourceHolder;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.mybatis.transaction.DebbieManagedTransactionFactory;
import com.truthbean.debbie.mybatis.transaction.SqlSessionHolder;
import com.truthbean.debbie.util.Assert;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

/**
 * Handles MyBatis SqlSession life cycle. It can register and get SqlSessions from Spring
 * {@code TransactionSynchronizationManager}. Also works if no transaction is active.
 *
 * @author Hunter Presnall
 * @author Eduardo Macarron
 */
public final class SqlSessionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionUtils.class);

    private static final String NO_EXECUTOR_TYPE_SPECIFIED = "No ExecutorType specified";
    private static final String NO_SQL_SESSION_FACTORY_SPECIFIED = "No SqlSessionFactory specified";
    private static final String NO_SQL_SESSION_SPECIFIED = "No SqlSession specified";

    /**
     * This class can't be instantiated, exposes static utility methods only.
     */
    private SqlSessionUtils() {
        // do nothing
    }

    /**
     * Creates a new MyBatis {@code SqlSession} from the {@code SqlSessionFactory} provided as a parameter and using its
     * {@code DataSource} and {@code ExecutorType}
     *
     * @param sessionFactory a MyBatis {@code SqlSessionFactory} to create new sessions
     * @param executorType executor type
     * @return a MyBatis {@code SqlSession}
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory, ExecutorType executorType) {

        Assert.notNull(sessionFactory, NO_SQL_SESSION_FACTORY_SPECIFIED);
        Assert.notNull(executorType, NO_EXECUTOR_TYPE_SPECIFIED);

        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo != null) {
            SqlSessionHolder holder = (SqlSessionHolder) transactionInfo.getResource(sessionFactory);

            SqlSession session = sessionHolder(executorType, holder);
            if (session != null) {
                return session;
            }
        }

        LOGGER.debug("Creating a new SqlSession");
        SqlSession session = sessionFactory.openSession(executorType);

        registerSessionHolder(sessionFactory, executorType, session);

        return session;
    }

    /**
     * Register session holder if synchronization is active (i.e. a Spring TX is active).
     * <p>
     * Note: The DataSource used by the Environment should be synchronized with the transaction either through
     * DataSourceTxMgr or another tx synchronization. Further assume that if an exception is thrown, whatever started the
     * transaction will handle closing / rolling back the Connection associated with the SqlSession.
     *
     * @param sessionFactory sqlSessionFactory used for registration.
     * @param executorType   executorType used for registration.
     * @param session        sqlSession used for registration.
     */
    private static void registerSessionHolder(SqlSessionFactory sessionFactory, ExecutorType executorType, SqlSession session) {
        SqlSessionHolder holder;
        Environment environment = sessionFactory.getConfiguration().getEnvironment();

        if (environment.getTransactionFactory() instanceof DebbieManagedTransactionFactory) {
            LOGGER.debug("Registering transaction synchronization for SqlSession [" + session + "]");

            holder = new SqlSessionHolder(session, executorType);
            TransactionManager.bindResource(sessionFactory, holder);
            TransactionManager.registerResourceHolder(new SqlSessionSynchronization(holder, sessionFactory));
        } else {
            LOGGER.debug("SqlSession [" + session + "] was not registered for synchronization because DataSource is not transactional");
        }

    }

    private static SqlSession sessionHolder(ExecutorType executorType, SqlSessionHolder holder) {
        SqlSession session = null;
        if (holder != null) {
            if (holder.getExecutorType() != executorType) {
                throw new RuntimeException("Cannot change the ExecutorType when there is an existing transaction");
            }

            LOGGER.debug("Fetched SqlSession [" + holder.getSqlSession() + "] from current transaction");
            session = holder.getSqlSession();
        }
        return session;
    }

    /**
     * Checks if {@code SqlSession} passed as an argument is managed by Spring {@code TransactionSynchronizationManager}
     * If it is not, it closes it, otherwise it just updates the reference counter and lets Spring call the close callback
     * when the managed transaction ends
     *
     * @param session        a target SqlSession
     * @param sessionFactory a factory of SqlSession
     */
    public static void closeSqlSession(SqlSession session, SqlSessionFactory sessionFactory) {
        Assert.notNull(session, NO_SQL_SESSION_SPECIFIED);
        Assert.notNull(sessionFactory, NO_SQL_SESSION_FACTORY_SPECIFIED);

        TransactionInfo transactionInfo = TransactionManager.peek();
        SqlSessionHolder holder = (SqlSessionHolder) transactionInfo.getResource(sessionFactory);
        if ((holder != null) && (holder.getSqlSession() == session)) {
            LOGGER.debug("Releasing transactional SqlSession [" + session + "]");
        } else {
            // LOGGER.debug("Closing non transactional SqlSession [" + session + "]");
            // session.close();
        }
    }

    /**
     * Returns if the {@code SqlSession} passed as an argument is being managed by Spring
     *
     * @param session        a MyBatis SqlSession to check
     * @param sessionFactory the SqlSessionFactory which the SqlSession was built with
     * @return true if session is transactional, otherwise false
     */
    public static boolean isSqlSessionTransactional(SqlSession session, SqlSessionFactory sessionFactory) {
        Assert.notNull(session, NO_SQL_SESSION_SPECIFIED);
        Assert.notNull(sessionFactory, NO_SQL_SESSION_FACTORY_SPECIFIED);

        TransactionInfo transactionInfo = TransactionManager.peek();
        SqlSessionHolder holder = (SqlSessionHolder) transactionInfo.getResource(sessionFactory);

        return (holder != null) && (holder.getSqlSession() == session);
    }

    /**
     * Callback for cleaning up resources. It cleans TransactionSynchronizationManager and also commits and closes the
     * {@code SqlSession}. It assumes that {@code Connection} life cycle will be managed by
     * {@code DataSourceTransactionManager} or {@code JtaTransactionManager}
     */
    private static final class SqlSessionSynchronization implements ResourceHolder {

        private final SqlSessionHolder holder;

        private final SqlSessionFactory sessionFactory;

        private boolean holderActive = true;

        public SqlSessionSynchronization(SqlSessionHolder holder, SqlSessionFactory sessionFactory) {
            Assert.notNull(holder, "Parameter 'holder' must be not null");
            Assert.notNull(sessionFactory, "Parameter 'sessionFactory' must be not null");

            this.holder = holder;
            this.sessionFactory = sessionFactory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getOrder() {
            // order right before any Connection synchronization
            return 10;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void prepare() {
            if (this.holderActive) {
                LOGGER.debug("Transaction synchronization resuming SqlSession [" + this.holder.getSqlSession() + "]");
                TransactionManager.bindResource(this.sessionFactory, this.holder);
            }
        }

        @Override
        public void beforeCommit() {
            // Connection commit or rollback will be handled by ConnectionSynchronization or
            // DataSourceTransactionManager.
            // But, do cleanup the SqlSession / Executor, including flushing BATCH statements so
            // they are actually executed.
            // SpringManagedTransaction will no-op the commit over the jdbc connection
            // TODO This updates 2nd level caches but the tx may be rolledback later on!
            /*try {
                LOGGER.debug("Transaction synchronization committing SqlSession [" + this.holder.getSqlSession() + "]");
                this.holder.getSqlSession().commit();
            } catch (PersistenceException p) {
                throw p;
            }*/
        }

        @Override
        public void afterCommit() {

        }

        @Override
        public void beforeRollback() {

        }

        @Override
        public void afterRollback() {

        }

        @Override
        public void beforeClose() {
            if (this.holderActive) {
                // afterCompletion may have been called from a different thread
                // so avoid failing if there is nothing in this one
                LOGGER.debug("Transaction synchronization deregistering SqlSession [" + this.holder.getSqlSession() + "]");
                this.holderActive = false;
                LOGGER.debug("Transaction synchronization closing SqlSession [" + this.holder.getSqlSession() + "]");
                this.holder.getSqlSession().close();
            }
        }

        @Override
        public void afterClose() {
        }
    }

}
