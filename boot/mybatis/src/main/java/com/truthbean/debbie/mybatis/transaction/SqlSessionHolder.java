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

import com.truthbean.debbie.util.Assert;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

public final class SqlSessionHolder {

  private final SqlSession sqlSession;

  private final ExecutorType executorType;

  /**
   * Creates a new holder instance.
   *
   * @param sqlSession
   *          the {@code SqlSession} has to be hold.
   * @param executorType
   *          the {@code ExecutorType} has to be hold.
   */
  public SqlSessionHolder(SqlSession sqlSession, ExecutorType executorType) {

    Assert.notNull(sqlSession, "SqlSession must not be null");
      Assert.notNull(executorType, "ExecutorType must not be null");

    this.sqlSession = sqlSession;
    this.executorType = executorType;
  }

  public SqlSession getSqlSession() {
    return sqlSession;
  }

  public ExecutorType getExecutorType() {
    return executorType;
  }
}
