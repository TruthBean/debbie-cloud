package com.truthbean.debbie.mybatis.transaction;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/26 18:15.
 */
public enum MybatisTransactionFactory {
    /**
     * org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
     */
    JDBC,
    /**
     * org.apache.ibatis.transaction.managed.ManagedTransactionFactory
     */
    MANAGED,
    /**
     * spring-mybatis
     */
    SPRING,
    /**
     * com.truthbean.debbie.mybatis.transaction.DebbieManagedTransactionFactory
     */
    DEBBIE
}
