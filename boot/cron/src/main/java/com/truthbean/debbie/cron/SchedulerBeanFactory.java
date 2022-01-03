/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.cron;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanCreatedException;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.env.EnvironmentContentHolder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-11 09:39
 */
public class SchedulerBeanFactory implements BeanFactory<Scheduler> {
    private volatile Scheduler scheduler;
    private final Set<String> beanNames;

    public SchedulerBeanFactory(String...names) {
        this.beanNames = new HashSet<>();
        Collections.addAll(this.beanNames, names);
    }

    @Override
    public Scheduler factoryBean(ApplicationContext applicationContext) {
        if (scheduler == null) {
            synchronized (SchedulerBeanFactory.class) {
                if (scheduler == null) {
                    EnvironmentContent envContent = applicationContext.getEnvContent();
                    if (envContent instanceof EnvironmentContentHolder) {
                        int i = Runtime.getRuntime().availableProcessors();
                        ((EnvironmentContentHolder)envContent).addProperty("org.quartz.threadPool.threadCount", String.valueOf(i));
                    }
                    try {
                        StdSchedulerFactory factory = new StdSchedulerFactory(envContent.getProperties());
                        this.scheduler = factory.getScheduler();
                    } catch (SchedulerException e) {
                        LOGGER.error("", e);
                        throw new BeanCreatedException(e);
                    }
                }
            }
        }
        return this.scheduler;
    }

    @Override
    public Scheduler factoryNamedBean(String name, ApplicationContext applicationContext) {
        return factoryBean(applicationContext);
    }

    @Override
    public Set<String> getBeanNames() {
        return beanNames;
    }

    @Override
    public Scheduler getCreatedBean() {
        return this.scheduler;
    }

    @Override
    public Class<?> getBeanClass() {
        return Scheduler.class;
    }

    @Override
    public boolean isCreated() {
        return this.scheduler != null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        if (this.scheduler != null) {
            try {
                if (this.scheduler.isStarted() && !this.scheduler.isShutdown()) {
                    this.scheduler.shutdown(true);
                }
            } catch (SchedulerException e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerBeanFactory.class);
}
