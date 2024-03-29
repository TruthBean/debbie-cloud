/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.undertow;

import com.truthbean.debbie.io.*;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.net.uri.UriUtils;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 11:01.
 */
public class UndertowRouterRequest implements RouterRequest {
    private final String id;
    private final HttpServerExchange exchange;

    private final Map<String, List<String>> headers = new HashMap<>();

    private final DefaultRouterRequest routerRequestCache = new DefaultRouterRequest();
    private UndertowRouterResponse routerResponse;

    public UndertowRouterRequest(HttpServerExchange exchange) {
        this(UUID.randomUUID().toString(), exchange);
    }

    private Charset charset;

    private UndertowRouterRequest(String id, HttpServerExchange exchange) {
        this.id = id;
        this.exchange = exchange;

        routerRequestCache.setMethod(HttpMethod.valueOf(exchange.getRequestMethod().toString()));
        routerRequestCache.setUrl(exchange.getRequestURI());

        routerRequestCache.setPathAttributes(new HashMap<>());
        setHeaders();

        setCookies();

        List<String> contentType = this.headers.get("Content-Type");
        setParams(contentType);
        setQueries();
    }

    private void setHeaders() {
        HeaderMap headerMap = exchange.getRequestHeaders();
        Map<String, List<String>> headers = new HashMap<>();
        if (headerMap != null) {
            // head
            Collection<HttpString> headerNames = headerMap.getHeaderNames();
            headerNames.forEach(httpString -> headers.put(httpString.toString(), headerMap.get(httpString)));

            var contentType = getMediaTypeFromHeaders(headerMap, "Content-Type");
            routerRequestCache.setContentType(contentType);
            var responseType = getMediaTypeFromHeaders(headerMap, "Response-Type");
            routerRequestCache.setResponseType(responseType);
        }
        this.headers.putAll(headers);
        routerRequestCache.setHeaders(headers);
    }

    private MediaTypeInfo getMediaTypeFromHeaders(HeaderMap headerMap, String name) {
        HeaderValues headerValues = headerMap.get(name);
        MediaTypeInfo type;
        if (headerValues != null && headerValues.element() != null) {
            type = MediaTypeInfo.parse(headerValues.element());
        } else {
            String ext = FileNameUtils.getExtension(getUrl());
            if (ext == null || ext.isBlank()) {
                type = MediaType.ANY.info();
            } else {
                type = MediaType.getTypeByUriExt(ext).info();
            }
        }
        return type;
    }

    private void setCookies() {
        var cookies = exchange.requestCookies();
        List<HttpCookie> result = new ArrayList<>();

        for (var cookie : cookies) {
            result.add(new UndertowRouterCookie(cookie).getHttpCookie());
        }
        routerRequestCache.setCookies(result);
    }

    public MediaType getMediaType(List<String> type) {
        if (type != null && !type.contains(MediaType.APPLICATION_FORM_URLENCODED.getValue())
                && !type.contains(MediaType.MULTIPART_FORM_DATA.getValue())) {
            if (type.contains(MediaType.TEXT_PLAIN.getValue())) {
                return MediaType.TEXT_PLAIN;
            } else if (type.contains(MediaType.APPLICATION_JSON.getValue())) {
                return MediaType.APPLICATION_JSON;
            } else if (type.contains(MediaType.APPLICATION_XML.getValue())) {
                return MediaType.APPLICATION_XML;
            } else if (type.contains(MediaType.APPLICATION_JAVASCRIPT.getValue())) {
                return MediaType.APPLICATION_JAVASCRIPT;
            } else if (type.contains(MediaType.TEXT_HTML.getValue())) {
                return MediaType.TEXT_HTML;
            } else {
                return MediaType.APPLICATION_OCTET_STREAM;
            }
        } else {
            return MediaType.ANY;
        }
    }

    public void setParams(List<String> contentType) {
        Map<String, List<Object>> parameters = new HashMap<>();
        exchange.getPathParameters().forEach((k, v) -> parameters.put(k, new ArrayList<>(v)));

        if (contentType != null && !contentType.isEmpty()) {
            if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED.getValue())
                    || contentType.contains(MediaType.MULTIPART_FORM_DATA.getValue())) {
                parameters.putAll(getFormData());
            }
        }

        parameters.putAll(getFormData());
        routerRequestCache.setParameters(parameters);
    }

    private Map<String, List<Object>> getFormData() {
        Map<String, List<Object>> parameters = new HashMap<>();
        try {
            FormParserFactory.Builder builder = FormParserFactory.builder();

            final FormDataParser formDataParser = builder.build().createParser(exchange);
            if (formDataParser != null) {
                exchange.startBlocking();
                FormData formData = formDataParser.parseBlocking();

                for (String data : formData) {
                    List<Object> dataValue = new ArrayList<>();
                    for (FormData.FormValue formValue : formData.get(data)) {
                        if (formValue.isFileItem()) {
                            FormData.FileItem fileItem = formValue.getFileItem();
                            MultipartFile file = new MultipartFile();
                            file.setContent(StreamHelper.toByteArray(fileItem.getInputStream()));
                            file.setFileName(formValue.getFileName());

                            dataValue.add(file);
                        } else {
                            dataValue.add(formValue.getValue());
                        }
                    }
                    parameters.put(data, dataValue);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
        return parameters;
    }

    private void setQueries() {
        Map<String, List<String>> queries = new HashMap<>();

        Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
        if (!queryParameters.isEmpty()) {
            queryParameters.forEach((k, v) -> queries.put(k, new ArrayList<>(v)));
        }
        routerRequestCache.setQueries(queries);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public HttpMethod getMethod() {
        return routerRequestCache.getMethod();
    }

    @Override
    public String getUrl() {
        return routerRequestCache.getUrl();
    }

    @Override
    public void addAttribute(String name, Object value) {
        routerRequestCache.addAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        routerRequestCache.removeAttribute(name);
    }

    @Override
    public Object getAttribute(String name) {
        return routerRequestCache.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return routerRequestCache.getAttributes();
    }

    @Override
    public Map<String, List<String>> getPathAttributes() {
        return routerRequestCache.getPathAttributes();
    }

    @Override
    public void setPathAttributes(Map<String, List<String>> map) {
        routerRequestCache.setPathAttributes(map);
    }

    @Override
    public Map<String, List<String>> getMatrix() {
        var matrix = routerRequestCache.getMatrix();
        if (matrix == null) {
            matrix = UriUtils.resolveMatrixByPath(getUrl());
            routerRequestCache.setMatrix(matrix);
        }
        return matrix;
    }

    @Override
    public HttpHeader getHeader() {
        return routerRequestCache.getHeader();
    }

    @Override
    public List<HttpCookie> getCookies() {
        return routerRequestCache.getCookies();
    }

    @Override
    public RouterSession getSession() {
        var session = routerRequestCache.getSession();
        if (session == null) {
            try {
                session = new UndertowRouterSession(exchange);
            } catch (Throwable throwable) {
                LOGGER.warn("this request has no session");
            }
            routerRequestCache.setSession(session);
        }
        return session;
    }

    @Override
    public Map<String, List<Object>> getParameters() {
        return routerRequestCache.getParameters();
    }

    @Override
    public Object getParameter(String name) {
        var parameters = routerRequestCache.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            var values = parameters.get(name);
            if (values != null && !values.isEmpty()) {
                return values.get(0);
            }
        }
        return null;
    }

    @Override
    public Map<String, List<String>> getQueries() {
        return routerRequestCache.getQueries();
    }

    @Override
    public InputStream getInputStreamBody() {
        var inputStreamBody = routerRequestCache.getInputStreamBody();
        if (inputStreamBody == null) {
            inputStreamBody = setInputStreamBody();
        }
        return inputStreamBody;
    }

    private InputStream setInputStreamBody() {
        exchange.startBlocking();
        return exchange.getInputStream();
    }

    @Override
    public MediaTypeInfo getContentType() {
        return routerRequestCache.getContentType();
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return routerRequestCache.getResponseType();
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getTextBody() {
        return null;
    }

    @Override
    public File getFileBody() {
        return null;
    }

    @Override
    public UndertowRouterRequest copy() {
        return new UndertowRouterRequest(id, exchange);
    }

    @Override
    public void setCharacterEncoding(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    @Override
    public String getRemoteAddress() {
        InetSocketAddress destinationAddress = exchange.getDestinationAddress();
        if (destinationAddress != null)
            return destinationAddress.toString();
        return null;
    }

    public UndertowRouterResponse getRouterResponse() {
        return routerResponse;
    }

    public void setRouterResponse(UndertowRouterResponse routerResponse) {
        this.routerResponse = routerResponse;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowRouterRequest.class);
}
