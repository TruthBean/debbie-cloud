/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.mybatis;

import java.util.List;
import java.util.Optional;

public interface SurnameService {

    boolean insert(Surname surname);

    /**
     *  test force commit and rollbackFor is not instanceOf this exception
     * @param surname params
     * @return boolean
     */
    boolean save(Surname surname);

    Optional<Surname> selectById(Long id);

    List<Surname> selectAll();

    void doNothing();
}
