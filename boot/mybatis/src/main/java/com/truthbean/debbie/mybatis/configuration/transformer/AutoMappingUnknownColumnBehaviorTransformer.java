/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis.configuration.transformer;

import com.truthbean.transformer.DataTransformer;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class AutoMappingUnknownColumnBehaviorTransformer implements DataTransformer<AutoMappingUnknownColumnBehavior, String> {
    @Override
    public String transform(AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
        return autoMappingUnknownColumnBehavior.name();
    }

    @Override
    public AutoMappingUnknownColumnBehavior reverse(String s) {
        return AutoMappingUnknownColumnBehavior.valueOf(s);
    }
}
