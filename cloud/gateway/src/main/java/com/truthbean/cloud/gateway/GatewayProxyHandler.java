/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.cloud.gateway;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.RouterResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Handles proxying of requests to backend services using java.net.http.HttpClient.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GatewayProxyHandler {

    private final HttpClient httpClient;
    private final GatewayConfiguration configuration;

    public GatewayProxyHandler(GatewayConfiguration configuration) {
        this.configuration = configuration;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(configuration.getConnectTimeout()))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Proxy the request to the backend service.
     */
    public void proxy(RouterRequest request, RouterResponse response, GatewayRoute route) {
        String targetUri = buildTargetUri(request, route.getUri());
        int attempts = 0;
        int maxRetries = configuration.getMaxRetries();

        while (attempts <= maxRetries) {
            try {
                HttpRequest proxyRequest = buildProxyRequest(request, targetUri);
                HttpResponse<byte[]> proxyResponse = httpClient.send(proxyRequest,
                        HttpResponse.BodyHandlers.ofByteArray());
                mapResponse(proxyResponse, response);
                LOGGER.debug("Proxy [{}] {} {} -> {} {}",
                        route.getId(), request.getMethod(), request.getUrl(), targetUri, proxyResponse.statusCode());
                return;
            } catch (Exception e) {
                attempts++;
                if (attempts > maxRetries) {
                    LOGGER.error("Proxy [{}] failed after {} retries: {}", route.getId(), maxRetries, e.getMessage());
                    response.setStatus(HttpStatus.BAD_GATEWAY.getStatus());
                    response.setResponseType(MediaType.TEXT_PLAIN_UTF8);
                    response.setContent("Bad Gateway: " + e.getMessage());
                    return;
                }
                LOGGER.warn("Proxy [{}] attempt {}/{} failed: {}", route.getId(), attempts, maxRetries, e.getMessage());
            }
        }
    }

    private String buildTargetUri(RouterRequest request, String routeUri) {
        String path = request.getUrl();
        StringBuilder sb = new StringBuilder(routeUri);
        if (path != null) {
            sb.append(path);
        }
        Map<String, List<String>> queries = request.getQueries();
        if (queries != null && !queries.isEmpty()) {
            sb.append('?');
            boolean first = true;
            for (Map.Entry<String, List<String>> entry : queries.entrySet()) {
                for (String value : entry.getValue()) {
                    if (!first) {
                        sb.append('&');
                    }
                    sb.append(entry.getKey()).append('=').append(value);
                    first = false;
                }
            }
        }
        return sb.toString();
    }

    private HttpRequest buildProxyRequest(RouterRequest request, String targetUri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(targetUri))
                .timeout(Duration.ofMillis(configuration.getReadTimeout()));

        // Copy headers
        var httpHeader = request.getHeader();
        if (httpHeader != null) {
            Map<String, List<String>> headers = httpHeader.getHeaders();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String key = entry.getKey();
                if (key != null && (key.equalsIgnoreCase("host") || key.equalsIgnoreCase("connection"))) {
                    continue;
                }
                for (String value : entry.getValue()) {
                    builder.header(key, value);
                }
            }
        }

        // Set method and body
        String method = request.getMethod().name();
        if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("HEAD")) {
            builder.GET();
        } else if (method.equalsIgnoreCase("DELETE")) {
            builder.DELETE();
        } else {
            var bodyStream = request.getInputStreamBody();
            byte[] body = new byte[0];
            if (bodyStream != null) {
                try {
                    body = bodyStream.readAllBytes();
                } catch (Exception ignored) {
                }
            }
            builder.method(method, HttpRequest.BodyPublishers.ofByteArray(body));
        }

        return builder.build();
    }

    private void mapResponse(HttpResponse<byte[]> proxyResponse, RouterResponse response) {
        response.setStatus(proxyResponse.statusCode());
        response.setResponseType(MediaType.APPLICATION_JSON_UTF8);

        // Copy response headers
        var headers = proxyResponse.headers().map();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (key != null && !key.equalsIgnoreCase("transfer-encoding")) {
                for (String value : entry.getValue()) {
                    response.addHeader(key, value);
                }
            }
        }

        // Set content type from response
        String contentType = proxyResponse.headers().firstValue("Content-Type").orElse(null);
        if (contentType != null) {
            response.setResponseType(MediaType.APPLICATION_JSON_UTF8);
        }

        byte[] body = proxyResponse.body();
        if (body != null) {
            response.setContent(body);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayProxyHandler.class);
}