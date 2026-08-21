/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mail;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MailModuleStarter implements DebbieModuleStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailModuleStarter.class);

    private static final String ENABLE_KEY = "debbie.mail.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        DebbieReflectionBeanFactory<MailConfiguration> debbieBeanInfo =
                new DebbieReflectionBeanFactory<>(MailConfiguration.class);
        debbieBeanInfo.addBeanName("mailConfiguration", MailConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(debbieBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        MailConfiguration configuration = factory.factory(MailConfiguration.class);
        MailSender mailSender = new MailSender(configuration);
        mailSender.init();

        var beanFactory = new SimpleBeanFactory<>(mailSender, MailSender.class);
        beanInfoManager.registerBeanInfo(beanFactory);
        LOGGER.info("Mail module started");
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 131110;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        MailSender mailSender = factory.factory(MailSender.class);
        if (mailSender != null) {
            mailSender.close();
        }
        LOGGER.info("Mail module released");
    }
}