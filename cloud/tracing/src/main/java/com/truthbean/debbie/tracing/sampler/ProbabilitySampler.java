/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tracing.sampler;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Probability-based sampler that samples a configurable fraction of traces.
 * <p>
 * A sampling rate of {@code 1.0} samples all traces (equivalent to
 * {@link AlwaysSampler}). A rate of {@code 0.0} samples none (equivalent
 * to {@link NeverSampler}).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ProbabilitySampler implements Sampler {

    private final double rate;

    public ProbabilitySampler(double rate) {
        this.rate = Math.max(0.0, Math.min(1.0, rate));
    }

    @Override
    public boolean shouldSample(String traceId) {
        if (rate <= 0.0) return false;
        if (rate >= 1.0) return true;
        return ThreadLocalRandom.current().nextDouble() < rate;
    }

    @Override
    public String name() {
        return "probability";
    }

    public double getRate() {
        return rate;
    }
}