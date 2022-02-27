/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.javafx;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.LoggerFactory;

import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-07-02 14:36.
 */
public class PrimaryStageHolder {
    private static PrimaryStage primaryStage;

    private PrimaryStageHolder() {
    }

    static void set(PrimaryStage primaryStage) {
        PrimaryStageHolder.primaryStage = primaryStage;
    }

    static void setPrimaryStage(BeanScanConfiguration configuration, GlobalBeanFactory globalBeanFactory) {
        Set<Class<?>> scannedClasses = configuration.getScannedClasses();
        for (Class<?> scannedClass : scannedClasses) {
            LOGGER.trace(() -> "scannedClass: " + scannedClass);
            if (PrimaryStage.class.isAssignableFrom(scannedClass)
                    && scannedClass.getAnnotation(DebbieJavaFx.class) != null) {
                primaryStage = (PrimaryStage) globalBeanFactory.factoryByNoBean(scannedClass);
            }
        }
    }

    public static PrimaryStage get() {
        return primaryStage;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryStageHolder.class);
}
