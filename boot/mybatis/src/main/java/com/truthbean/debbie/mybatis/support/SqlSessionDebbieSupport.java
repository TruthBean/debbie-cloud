/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis.support;

import com.truthbean.debbie.mybatis.SqlSessionTemplate;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public abstract class SqlSessionDebbieSupport {
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * Set MyBatis SqlSessionFactory to be used by this DAO. Will automatically create SqlSessionTemplate for the given
     * SqlSessionFactory.
     *
     * @param sqlSessionFactory
     *          a factory of SqlSession
     */
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        if (this.sqlSessionTemplate == null || sqlSessionFactory != this.sqlSessionTemplate.getSqlSessionFactory()) {
            this.sqlSessionTemplate = createSqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * Create a SqlSessionTemplate for the given SqlSessionFactory. Only invoked if populating the DAO with a
     * SqlSessionFactory reference!
     * <p>
     * Can be overridden in subclasses to provide a SqlSessionTemplate instance with different configuration, or a custom
     * SqlSessionTemplate subclass.
     *
     * @param sqlSessionFactory
     *          the MyBatis SqlSessionFactory to create a SqlSessionTemplate for
     * @return the new SqlSessionTemplate instance
     * @see #setSqlSessionFactory
     */
    @SuppressWarnings("WeakerAccess")
    protected SqlSessionTemplate createSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * Return the MyBatis SqlSessionFactory used by this DAO.
     *
     * @return a factory of SqlSession
     */
    public final SqlSessionFactory getSqlSessionFactory() {
        return (this.sqlSessionTemplate != null ? this.sqlSessionTemplate.getSqlSessionFactory() : null);
    }

    /**
     * Set the SqlSessionTemplate for this DAO explicitly, as an alternative to specifying a SqlSessionFactory.
     *
     * @param sqlSessionTemplate
     *          a template of SqlSession
     * @see #setSqlSessionFactory
     */
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    /**
     * Users should use this method to get a SqlSession to call its statement methods This is SqlSession is managed by
     * spring. Users should not commit/rollback/close it because it will be automatically done.
     *
     * @return Spring managed thread safe SqlSession
     */
    public SqlSession getSqlSession() {
        return this.sqlSessionTemplate;
    }

    /**
     * Return the SqlSessionTemplate for this DAO, pre-initialized with the SessionFactory or set explicitly.
     * <p>
     * <b>Note: The returned SqlSessionTemplate is a shared instance.</b> You may introspect its configuration, but not
     * modify the configuration Consider creating a custom
     * SqlSessionTemplate instance via {@code new SqlSessionTemplate(getSqlSessionFactory())}, in which case you're
     * allowed to customize the settings on the resulting instance.
     *
     * @return a template of SqlSession
     */
    public SqlSessionTemplate getSqlSessionTemplate() {
        return this.sqlSessionTemplate;
    }
}
