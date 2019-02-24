package com.mycloud.demo.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 前置过滤器<br>
 * 检查每个传入的请求，如果"tmx-correlation-id"/"tmx-user-id"不存在，则在HTTP首部中创建。<br>
 * tmx-xxx-id通过http header跨系统具体流程：<br>
 * (1) 用户的Request通过Zuul访问APP-SALARY，通过Zuul的前置过滤器TrackingFilter，如果Request的Header中没有tmx-xxx-id，则由前置过滤器生成一个新的<br>
 * (2) APP-SALARY的UserContextFilter拦截网关中的Header中tmx-xxx-id并放入由ThreadLocal持有的UserContext中<br>
 * (3) APP-SALARY通过RestTemplate访问APP-TAX-CALC时，设置拦截器UserContextInterceptor到RestTemplate中，<br>
 * 它把tmx-xxx-id设定到Header中传到APP-TAX-CALC<br>
 */
@Component
public class PreFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(PreFilter.class);

    @Autowired
    private FilterUtils filterUtils;

    /**
     * PRE_FILTER_TYPE(pre): 前置过滤器<br>
     * ROUTE_FILTER_TYPE(route): 路由过滤器<br>
     * POST_FILTER_TYPE(post): 后置过滤器<br>
     * 
     * @see com.netflix.zuul.ZuulFilter#filterType()
     */
    @Override
    public String filterType() {
        return FilterUtils.PRE_FILTER_TYPE;
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
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    /**
     * run()是每次服务通过过滤器时执行的代码<br>
     * 
     * @see com.netflix.zuul.IZuulFilter#run()
     */
    public Object run() {

        logger.debug("----- Pre Filter -----");

        fillIfEmpty(FilterUtils.CORRELATION_ID, filterUtils.getCorrelationId());
        fillIfEmpty(FilterUtils.USER_ID, filterUtils.getUserId());

        RequestContext ctx = RequestContext.getCurrentContext();
        logger.debug("Processing incoming request for {}.", ctx.getRequest().getRequestURI());

        return null;
    }

    /**
     * Fill the value of 'tmx-correlation-id' and 'tmx-user-id' if empty from header of incoming request.<br>
     * 
     * @param name
     * @param value
     */
    private void fillIfEmpty(String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            logger.debug(String.format("'%s' found in tracking filter: %s. ", name, value));
        } else {
            String id = filterUtils.getCurrentDateStr();
            if (FilterUtils.CORRELATION_ID.equals(name)) {
                filterUtils.setCorrelationId(StringUtils.join(FilterUtils.CORRELATION_ID, "_", id));
                logger.debug(
                        String.format("'%s' generated in tracking filter: %s. ", name, filterUtils.getCorrelationId()));
            } else if (FilterUtils.USER_ID.equals(name)) {
                filterUtils.setUserId(StringUtils.join(FilterUtils.USER_ID, "_", id));
                logger.debug(String.format("'%s' generated in tracking filter: %s. ", name, filterUtils.getUserId()));
            }
        }
    }
}
