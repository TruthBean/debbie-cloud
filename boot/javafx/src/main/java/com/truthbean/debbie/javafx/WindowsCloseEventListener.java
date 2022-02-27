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
import com.truthbean.debbie.event.ApplicationExitEvent;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.LoggerFactory;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-07-02 11:58.
 */
public class WindowsCloseEventListener implements EventHandler<WindowEvent> {

    private final DebbieEventPublisher eventPublisher;
    private WindowsCloseEventListener(DebbieEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    private static WindowsCloseEventListener instance;
    public static void createInstance(DebbieEventPublisher eventPublisher) {
        if (instance == null) {
            synchronized (WindowsCloseEventListener.class) {
                if (instance == null) {
                    instance = new WindowsCloseEventListener(eventPublisher);
                }
            }
        }
    }

    public static WindowsCloseEventListener getInstance() {
        LOGGER.debug("getInstance with eventPublisher: " + instance.eventPublisher);
        return instance;
    }

    @Override
    public void handle(WindowEvent event) {
        LOGGER.info("windowEvent ... ");
        eventPublisher.publishEvent(new ApplicationExitEvent(this, JavaFxApplication.getApplication()));
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(WindowsCloseEventListener.class);
}
