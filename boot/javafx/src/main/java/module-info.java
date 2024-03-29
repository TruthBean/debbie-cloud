/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-01-27 18:48
 */
module com.truthbean.debbie.javafx {
    requires java.base;
    requires java.management;
    requires transitive com.truthbean.debbie.core;
    requires transitive javafx.graphics;
    // requires javafx.controlsEmpty;
    requires transitive javafx.controls;
    // requires javafx.graphicsEmpty;
    // requires javafx.baseEmpty;
    requires transitive javafx.fxml;

    exports com.truthbean.debbie.javafx;

    provides com.truthbean.debbie.boot.AbstractApplication with com.truthbean.debbie.javafx.JavaFxApplication;
    provides com.truthbean.debbie.boot.DebbieModuleStarter with com.truthbean.debbie.javafx.JavaFxModuleStarter;
}