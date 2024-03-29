/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis;

import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.mybatis.transaction.MybatisTransactionInfo;
import org.apache.ibatis.session.SqlSession;

/**
 * @author truthbean
 * @since 0.0.2
 */
public interface SqlSessionService {
    default TransactionInfo getTransaction() {
        return TransactionManager.peek();
    }

    /*default SqlSession getSqlSession() {
        MybatisTransactionInfo transactionInfo = (MybatisTransactionInfo) TransactionManager.peek();
        return transactionInfo.getSession();
    }*/
}
