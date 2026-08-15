/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes;

/**
 * Exception thrown when a Kubernetes API call fails.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public KubernetesException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public KubernetesException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public KubernetesException(int statusCode, String message) {
        super("kubernetes api error [" + statusCode + "]: " + message);
        this.statusCode = statusCode;
    }

    public KubernetesException(int statusCode, String message, Throwable cause) {
        super("kubernetes api error [" + statusCode + "]: " + message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}