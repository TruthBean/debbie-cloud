/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka.client;

import com.truthbean.debbie.eureka.model.InstanceInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Periodically sends heartbeats (renewals) to the Eureka server
 * for a registered instance.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EurekaHeartbeatScheduler {

    private final EurekaClient client;
    private final InstanceInfo instance;
    private final long intervalMillis;
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> task;
    private volatile boolean running;

    public EurekaHeartbeatScheduler(EurekaClient client, InstanceInfo instance, long intervalMillis) {
        this.client = client;
        this.instance = instance;
        this.intervalMillis = intervalMillis;
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            var t = new Thread(r, "eureka-heartbeat");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        if (running) return;
        running = true;
        task = executor.scheduleAtFixedRate(this::sendHeartbeat,
                intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (!running) return;
        running = false;
        if (task != null) task.cancel(false);
        executor.shutdown();
    }

    public boolean isRunning() { return running; }

    public long getIntervalMillis() { return intervalMillis; }

    private void sendHeartbeat() {
        try {
            var ok = client.renew(instance.getAppName(), instance.getInstanceId());
            if (!ok) {
                client.register(instance);
            }
        } catch (Exception e) {
            // ignore transient failures
        }
    }
}