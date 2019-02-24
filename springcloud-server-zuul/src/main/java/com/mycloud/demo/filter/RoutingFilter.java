package com.mycloud.demo.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

/**
 * 路由过滤器
 *
 */
@Component
public class RoutingFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final Logger logger = LoggerFactory.getLogger(RoutingFilter.class);

    @Autowired
    private ProxyRequestHelper helper;

    /**
     * PRE_FILTER_TYPE(pre): 前置过滤器<br>
     * ROUTE_FILTER_TYPE(route): 路由过滤器<br>
     * POST_FILTER_TYPE(post): 后置过滤器<br>
     * 
     * @see com.netflix.zuul.ZuulFilter#filterType()
     */
    @Override
    public String filterType() {
        return FilterUtils.ROUTE_FILTER_TYPE;
    }

    /**
     * 过滤器的执行顺序<br>
     * 
     * @see com.netflix.zuul.ZuulFilter#filterOrder()
     */
    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    /**
     * 是否执行过滤器，只有"app-tax-calc"才会执行<br>
     * 
     * @see com.netflix.zuul.IZuulFilter#shouldFilter()
     */
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (ctx.getRouteHost() == null && ctx.get(SERVICE_ID_KEY) != null
                && "app-salary".equals(ctx.get(SERVICE_ID_KEY)) && ctx.sendZuulResponse());
    }

    /**
     * @see com.netflix.zuul.IZuulFilter#run()
     */
    @Override
    public Object run() {

        logger.debug("----- Routing Filter -----");

        // Get Zuul RequestContext and HttpServletRequest
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        // (1) Get [Method]
        String method = request.getMethod();
        // (2) Get [URI]
        String url = getUrl(request);
        // (3) Get [Header]
        Headers.Builder headers = getHeaders(request);
        // (4) Get [Body]
        RequestBody requestBody = getRequestBody(request, method, headers);

        // Build request builder by (1)(2)(3)(4)
        Request.Builder builder = new Request.Builder().headers(headers.build()).url(url).method(method, requestBody);

        // Create a new OkHttpClient
        OkHttpClient httpClient = new OkHttpClient.Builder().build();

        // Execute and get response
        Response response = execute(httpClient, builder);

        // Set response to Zuul RequestContext
        setResponse(response);

        // prevent SimpleHostRoutingFilter from running [?]
        context.setRouteHost(null);

        return null;
    }

    private String getUrl(HttpServletRequest request) {
        String uri = helper.buildZuulRequestURI(request);
        return StringUtils.join("http://localhost:9221", uri);
    }

    private Headers.Builder getHeaders(HttpServletRequest request) {

        Headers.Builder headers = new Headers.Builder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                headers.add(name, value);
            }
        }

        return headers;
    }

    private RequestBody getRequestBody(HttpServletRequest request, String method, Headers.Builder headers) {

        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
        } catch (IOException ex) {
            // no requestBody is ok.
        }

        RequestBody requestBody = null;
        if (inputStream != null && HttpMethod.permitsRequestBody(method)) {
            MediaType mediaType = null;
            if (headers.get("Content-Type") != null) {
                mediaType = MediaType.parse(headers.get("Content-Type"));
            }
            requestBody = RequestBody.create(mediaType, getByteContent(inputStream));
        }

        return requestBody;
    }

    private byte[] getByteContent(InputStream inputStream) {

        byte[] result = null;
        try {
            result = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            // no requestBody is ok.
        }

        return result;
    }

    private Response execute(OkHttpClient httpClient, Request.Builder builder) {

        Response response = null;
        try {
            response = httpClient.newCall(builder.build()).execute();
        } catch (IOException e) {
            // no requestBody is ok.
        }

        return response;
    }

    private void setResponse(Response response) {

        LinkedMultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();

        for (Map.Entry<String, List<String>> entry : response.headers().toMultimap().entrySet()) {
            responseHeaders.put(entry.getKey(), entry.getValue());
        }

        try {
            this.helper.setResponse(response.code(), response.body().byteStream(), responseHeaders);
        } catch (IOException e) {
            // no requestBody is ok.
        }
    }
}
