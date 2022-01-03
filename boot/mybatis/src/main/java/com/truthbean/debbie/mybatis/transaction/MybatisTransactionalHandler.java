/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis.transaction;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.proxy.MethodProxyHandler;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.transaction.MethodNoJdbcTransactionalException;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;

import org.apache.ibatis.session.SqlSessionFactory;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class MybatisTransactionalHandler implements MethodProxyHandler<JdbcTransactional> {
    private final MybatisTransactionInfo transactionInfo;

    private JdbcTransactional jdbcTransactional;
    private JdbcTransactional classJdbcTransactional;

    private int order;

    private ApplicationContext applicationContext;

    private boolean autoCommit;

    public MybatisTransactionalHandler() {
        this.transactionInfo = new MybatisTransactionInfo();
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean exclusive() {
        return true;
    }

    @Override
    public void setMethodAnnotation(JdbcTransactional methodAnnotation) {
        this.jdbcTransactional = methodAnnotation;
    }

    @Override
    public void setClassAnnotation(JdbcTransactional classAnnotation) {
        this.classJdbcTransactional = classAnnotation;
    }

    @Override
    public void setMethod(Method method) {
        transactionInfo.bindMethod(method);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    @Override
    public void before() {
        LOGGER.debug("running before method (" + transactionInfo.getMethod() + ") invoke ..");
        SqlSessionFactory sqlSessionFactory = applicationContext.factory(SqlSessionFactory.class);

        TransactionInfo peek = null;
        try {
            peek = TransactionManager.peek();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (peek == null) {
            // DataSourceFactory dataSourceFactory = beanInitialization.getRegisterBean(DataSourceFactory.class);
            // 创建mapper的connection与事务的connection不是同一个
            // transactionInfo.setConnection(dataSourceFactory.getConnection());
        } else {
            transactionInfo.setConnection(peek.getConnection());
            TransactionManager.remove();
        }

        if (jdbcTransactional == null && classJdbcTransactional == null) {
            throw new MethodNoJdbcTransactionalException();
        } else if (jdbcTransactional == null && !classJdbcTransactional.readonly()) {
            transactionInfo.setAutoCommit(false);
            autoCommit = false;
            transactionInfo.setForceCommit(classJdbcTransactional.forceCommit());
            transactionInfo.setRollbackFor(classJdbcTransactional.rollbackFor());
        } else if (jdbcTransactional != null && !jdbcTransactional.readonly()) {
            transactionInfo.setAutoCommit(false);
            autoCommit = false;
            transactionInfo.setForceCommit(jdbcTransactional.forceCommit());
            transactionInfo.setRollbackFor(jdbcTransactional.rollbackFor());
        } else {
            transactionInfo.setAutoCommit(true);
            autoCommit = true;
        }
        // SqlSession session = sqlSessionFactory.openSession(transactionInfo.getConnection());
        // transactionInfo.setSession(session);
        TransactionManager.offer(transactionInfo);
    }

    @Override
    public void after() {
        LOGGER.debug("running after method (" + transactionInfo.getMethod() + ") invoke ..");
        if (!autoCommit) {
            transactionInfo.commit();
        }
    }

    @Override
    public void catchException(Throwable e) throws Throwable {
        LOGGER.debug("running when method (" + transactionInfo.getMethod() + ") invoke throw exception and catched ..");
        if (!autoCommit) {
            if (transactionInfo.isForceCommit()) {
                LOGGER.debug("force commit ..");
                transactionInfo.commit();
            } else {
                if (transactionInfo.getRollbackFor().isInstance(e)) {
                    transactionInfo.rollback();
                    LOGGER.debug("rollback ..");
                } else {
                    LOGGER.debug("not rollback for this exception(" + e.getClass().getName() + "), it committed");
                    transactionInfo.commit();
                }
            }
        }
        throw e;
    }

    @Override
    public void finallyRun() {
        LOGGER.debug("running when method (" + transactionInfo.getMethod() + ") invoked and run to finally ..");
        transactionInfo.close();
        TransactionManager.remove();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisTransactionalHandler.class);
}
