/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.cron;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.task.MethodTaskAction;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.task.TaskAction;
import com.truthbean.debbie.task.TaskInfo;
import org.quartz.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-11 11:27
 */
public class CronTaskAction extends MethodTaskAction implements TaskAction {
    private GlobalBeanFactory globalBeanFactory;
    private boolean enable;
    private final DebbieCronJobFactory jobFactory = new DebbieCronJobFactory();

    @Override
    protected String getTaskThreadName() {
        return "MethodCronTask";
    }

    @Override
    public void prepare() {
        if (!enable) return;
        super.prepare();

        Scheduler scheduler = globalBeanFactory.factory(Scheduler.class);
        for (TaskInfo jobInfo : taskList) {
            JobDetail job = JobBuilder.newJob(SchedulerJobProxy.class)
                    .withIdentity("job-" + jobInfo.getTaskName(), jobInfo.getTaskGroup())
                    .build();
            jobFactory.addJobInfo(job.getKey(), jobInfo);

            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger-" + jobInfo.getTaskName(), jobInfo.getTaskGroup())
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getTaskConfig().getCron()))
                    .build();

            try {
                scheduler.setJobFactory(jobFactory);
                scheduler.scheduleJob(job, cronTrigger);
            } catch (SchedulerException e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    public void doTask() {
        if (!enable) return;
        Scheduler scheduler = globalBeanFactory.factory(Scheduler.class);
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        globalBeanFactory = applicationContext.getGlobalBeanFactory();
        enable = applicationContext.getDefaultEnvironment().getBooleanValue("debbie.quartz.enable", true);
    }

    @Override
    public void stop() {
        if (!enable) return;
        Scheduler scheduler = globalBeanFactory.factory(Scheduler.class);
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            LOGGER.error("", e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CronTaskAction.class);
}
