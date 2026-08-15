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

/**
 * Factory for creating samplers by name.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class SamplerFactory {

    public static final String ALWAYS = "always";
    public static final String NEVER = "never";
    public static final String PROBABILITY = "probability";

    private SamplerFactory() {}

    public static Sampler create(String strategy) {
        if (strategy == null || strategy.isBlank()) {
            return new AlwaysSampler();
        }
        return switch (strategy.trim().toLowerCase()) {
            case ALWAYS -> new AlwaysSampler();
            case NEVER -> new NeverSampler();
            case PROBABILITY -> new ProbabilitySampler(0.1);
            default -> new AlwaysSampler();
        };
    }

    public static Sampler create(String strategy, double rate) {
        if (strategy == null || strategy.isBlank()) {
            return new AlwaysSampler();
        }
        return switch (strategy.trim().toLowerCase()) {
            case ALWAYS -> new AlwaysSampler();
            case NEVER -> new NeverSampler();
            case PROBABILITY -> new ProbabilitySampler(rate);
            default -> new AlwaysSampler();
        };
    }

    public static Sampler createDefault() {
        return new AlwaysSampler();
    }
}