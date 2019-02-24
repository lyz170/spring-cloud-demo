package com.mycloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * URLs:<br>
 * http://localhost:9131/server-zuul/actuator/routes<br>
 * http://localhost:9131/server-zuul/actuator/filters<br>
 * Zuul自带的核心过滤器:<br>
 * Pre:<br>
 * - ServletDetectionFilter(-3): 该过滤器总是会被执行，主要用来检测当前请求是通过Spring的DispatcherServlet处理运行，还是通过ZuulServlet来处理运行的。<br>
 * 它的检测结果会以布尔类型保存在当前请求上下文的isDispatcherServletRequest参数中，这样在后续的过滤器中，我们就可以通过RequestUtils.isDispatcherServletRequest()和RequestUtils.isZuulServletRequest()方法判断它以实现做不同的处理。<br>
 * - Servlet30WrapperFilter(-2): 目前的实现会对所有请求生效，主要为了将原始的HttpServletRequest包装成Servlet30RequestWrapper对象。 <br>
 * - FormBodyWrapperFilter(-1): 该过滤器仅对两种类请求生效，第一类是Content-Type为application/x-www-form-urlencoded的请求，<br>
 * 第二类是Content-Type为multipart/form-data并且是由Spring的DispatcherServlet处理的请求（用到了ServletDetectionFilter的处理结果）。<br>
 * - DebugFilter(1): 该过滤器会根据配置参数zuul.debug.request和请求中的debug参数来决定是否执行过滤器中的操作。<br>
 * 而它的具体操作内容则是将当前的请求上下文中的debugRouting和debugRequest参数设置为true。<br>
 * 由于在同一个请求的不同生命周期中，都可以访问到这两个值，所以我们在后续的各个过滤器中可以利用这两值来定义一些debug信息，这样当线上环境出现问题的时候，可以通过请求参数的方式来激活这些debug信息以帮助分析问题。<br>
 * - PreDecorationFilter(5): 该过滤器会判断当前请求上下文中是否存在forward.to和serviceId参数，<br>
 * 如果都不存在，那么它就会执行具体过滤器的操作（如果有一个存在的话，说明当前请求已经被处理过了，因为这两个信息就是根据当前请求的路由信息加载进来的）。 <br>
 * Route:<br>
 * - RibbonRoutingFilter(10): 该过滤器只对请求上下文中存在serviceId参数的请求进行处理，即只对通过serviceId配置路由规则的请求生效。<br>
 * 而该过滤器的执行逻辑就是面向服务路由的核心，它通过使用Ribbon和Hystrix来向服务实例发起请求，并将服务实例的请求结果返回。<br>
 * - SimpleHostRoutingFilter(100): 该过滤器只对请求上下文中存在routeHost参数的请求进行处理，即只对通过url配置路由规则的请求生效。<br>
 * 而该过滤器的执行逻辑就是直接向routeHost参数的物理地址发起请求，从源码中我们可以知道该请求是直接通过httpclient包实现的，而没有使用Hystrix命令进行包装，所以这类请求并没有线程隔离和断路器的保护。 <br>
 * - SendForwardFilter(500): 该过滤器只对请求上下文中存在forward.to参数的请求进行处理，即用来处理路由规则中的forward本地跳转配置。<br>
 * Post:<br>
 * - SendErrorFilter(0): 该过滤器仅在请求上下文中包含error.status_code参数（由之前执行的过滤器设置的错误编码）并且还没有被该过滤器处理过的时候执行。<br>
 * 而该过滤器的具体逻辑就是利用请求上下文中的错误信息来组织成一个forward到API网关/error错误端点的请求来产生错误响应。<br>
 * - SendResponseFilter(1000): 该过滤器会检查请求上下文中是否包含请求响应相关的头信息、响应数据流或是响应体，只有在包含它们其中一个的时候就会执行处理逻辑。<br>
 * 而该过滤器的处理逻辑就是利用请求上下文的响应信息来组织需要发送回客户端的响应内容。<br>
 */
@SpringBootApplication
@EnableZuulProxy
public class ZuulServerApplication implements CommandLineRunner {

    @Value("${app.property.env}")
    public String ENVIRONMENT;

    private static final Logger logger = LoggerFactory.getLogger(ZuulServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZuulServerApplication.class, args);
        logger.info("<<<<<<<<<< Application[springcloud-server-zuul] Started >>>>>>>>>>");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(String.format("<<<<<<<<<< Environment : %s >>>>>>>>>>", ENVIRONMENT.toUpperCase()));
    }
}