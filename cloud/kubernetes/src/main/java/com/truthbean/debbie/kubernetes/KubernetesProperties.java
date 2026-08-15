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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Kubernetes connection properties, auto-detected from the pod environment.
 * <p>
 * When running inside a Kubernetes pod, the following are available:
 * <ul>
 *   <li>Env {@code KUBERNETES_SERVICE_HOST} and {@code KUBERNETES_SERVICE_PORT}</li>
 *   <li>File {@code /var/run/secrets/kubernetes.io/serviceaccount/token}</li>
 *   <li>File {@code /var/run/secrets/kubernetes.io/serviceaccount/namespace}</li>
 *   <li>File {@code /var/run/secrets/kubernetes.io/serviceaccount/ca.crt}</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesProperties {

    private static final String SA_DIR = "/var/run/secrets/kubernetes.io/serviceaccount";

    private String apiServerHost;
    private int apiServerPort;
    private String namespace;
    private String token;
    private String caCertPath;
    private boolean insidePod;

    public KubernetesProperties() {
    }

    public static KubernetesProperties autoDetect() {
        var props = new KubernetesProperties();
        var host = System.getenv("KUBERNETES_SERVICE_HOST");
        var port = System.getenv("KUBERNETES_SERVICE_PORT");

        if (host != null && !host.isEmpty()) {
            props.setApiServerHost(host);
            props.setApiServerPort(port != null ? Integer.parseInt(port) : 443);
            props.setInsidePod(true);
        } else {
            props.setApiServerHost("localhost");
            props.setApiServerPort(8443);
            props.setInsidePod(false);
        }

        props.setNamespace(readFile(Paths.get(SA_DIR, "namespace"), "default"));
        props.setToken(readFile(Paths.get(SA_DIR, "token"), null));

        var caCert = Paths.get(SA_DIR, "ca.crt");
        props.setCaCertPath(Files.exists(caCert) ? caCert.toString() : null);

        return props;
    }

    public static KubernetesProperties of(String host, int port, String namespace, String token) {
        var props = new KubernetesProperties();
        props.setApiServerHost(host);
        props.setApiServerPort(port);
        props.setNamespace(namespace);
        props.setToken(token);
        props.setInsidePod(false);
        return props;
    }

    public String getApiServerHost() { return apiServerHost; }
    public void setApiServerHost(String apiServerHost) { this.apiServerHost = apiServerHost; }

    public int getApiServerPort() { return apiServerPort; }
    public void setApiServerPort(int apiServerPort) { this.apiServerPort = apiServerPort; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getCaCertPath() { return caCertPath; }
    public void setCaCertPath(String caCertPath) { this.caCertPath = caCertPath; }

    public boolean isInsidePod() { return insidePod; }
    public void setInsidePod(boolean insidePod) { this.insidePod = insidePod; }

    public String getBaseUrl() {
        return "https://" + apiServerHost + ":" + apiServerPort;
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }

    private static String readFile(Path path, String defaultValue) {
        try {
            if (Files.exists(path)) {
                return Files.readString(path, StandardCharsets.UTF_8).trim();
            }
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return "KubernetesProperties{host=" + apiServerHost + ", port=" + apiServerPort
                + ", namespace=" + namespace + ", insidePod=" + insidePod
                + ", hasToken=" + hasToken() + '}';
    }
}