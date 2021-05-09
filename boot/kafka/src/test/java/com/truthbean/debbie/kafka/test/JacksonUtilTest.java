/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kafka.test;

import com.truthbean.debbie.kafka.KafkaConfiguration;
import com.truthbean.debbie.util.JacksonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-27 18:47
 */
public class JacksonUtilTest {

    @Test
    public void yamlToBean() throws IOException {
        URL resource = JacksonUtilTest.class.getClassLoader().getResource("application.yaml");
        KafkaConfiguration configuration = JacksonUtils.yamlStreamToBean(resource.openStream(), KafkaConfiguration.class);
        System.out.println(configuration);
    }
}
