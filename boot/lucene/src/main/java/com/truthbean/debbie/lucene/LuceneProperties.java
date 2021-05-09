/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.lucene;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContentHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.net.URL;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-13 20:00
 */
public class LuceneProperties extends EnvironmentContentHolder implements DebbieProperties<LuceneConfiguration> {

    private static final String DATA_PATH = "debbie.lucene.data.path";

    private final LuceneConfiguration configuration;
    public LuceneProperties() {
        this.configuration = new LuceneConfiguration();
        URL resource = LuceneProperties.class.getResource(".");
        String path = resource.getPath();
        this.configuration.setDataPath(super.getStringValue(DATA_PATH, path));
    }

    @Override
    public LuceneConfiguration toConfiguration(ApplicationContext applicationContext) {
        return configuration;
    }
}
