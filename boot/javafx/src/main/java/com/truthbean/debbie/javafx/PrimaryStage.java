/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.javafx;

import javafx.stage.Stage;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-07-02 13:57.
 */
public interface PrimaryStage {
    void init(Stage primaryStage) throws Exception;

    default Stage getStage() {
        return null;
    }
}
