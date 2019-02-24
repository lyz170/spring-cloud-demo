package com.mycloud.demo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 后置过滤器<br>
 * 在HTTP Response中返回"tmx-correlation-id"<br>
 */
@Component
public class ResponseFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Autowired
    FilterUtils filterUtils;

    /**
     * PRE_FILTER_TYPE(pre): 前置过滤器<br>
     * ROUTE_FILTER_TYPE(route): 路由过滤器<br>
     * POST_FILTER_TYPE(post): 后置过滤器<br>
     * 
     * @see com.netflix.zuul.ZuulFilter#filterType()
     */
    @Override
    public String filterType() {
        return FilterUtils.POST_FILTER_TYPE;
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
     * 是否执行过滤器<br>
     * 
     * @see com.netflix.zuul.IZuulFilter#shouldFilter()
     */
    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    /**
     * @see com.netflix.zuul.IZuulFilter#run()
     */
    @Override
    public Object run() {

        logger.debug("----- Response Filter -----");

        RequestContext ctx = RequestContext.getCurrentContext();
        logger.debug("Adding the correlation id to the outbound headers. {}", filterUtils.getCorrelationId());
        ctx.getResponse().addHeader(FilterUtils.CORRELATION_ID, filterUtils.getCorrelationId());
        logger.debug("Completing outgoing request for {}.", ctx.getRequest().getRequestURI());

        return null;
    }
}
