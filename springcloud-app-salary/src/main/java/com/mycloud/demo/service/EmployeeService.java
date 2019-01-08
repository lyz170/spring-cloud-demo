package com.mycloud.demo.service;

import com.mycloud.demo.config.AppException;
import com.mycloud.demo.entity.Employee;
import com.mycloud.demo.repository.EmployeeRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    private final static String ERR1 = "Hystrix timeout when calling '%s' method.";

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * (1) 使用[@HystrixCommand]注解表示该方法由Hystrix断路器进行管理。<br>
     * 被[@HystrixCommand]注解标注的方法会被AOP拦截，具体逻辑在HystrixCommandAspect.java中。<br>
     * 这里的例子主要有以下两个：<br>
     * - EmployeeService: Hystrix包装所有的数据库调用<br>
     * - CallAppTaxCalcService: Hystrix包装所有的内部服务调用<br>
     * (2) 使用方法：<br>
     * 当Spring框架检测到该注解时，将生成一个动态代理包装该方法，并用过专门用于处理远程调用的线程池来管理对该方法的所有调用。<br>
     * 使用默认的@HystrixCommand时，它将把所有线程放到同一个线程池中，有可能会出现问题，最好使用舱壁模式。<br>
     * 使用[commandProperties]可以设置参数：<br>
     * - execution.isolation.thread.timeoutInMilliseconds：超时时间<br>
     * - circuitBreaker.requestVolumeThreshold: 当Hystrix遇到服务错误时，它将开始一个10s的计时器，用于检查服务调用次数和故障百分比，
     * 如果10s内没有达到最小调用数量[参数]，那么即使有几个（甚至全部）调用失败，Hystrix也不会采取行动。<br>
     * - circuitBreaker.errorThresholdPercentage: 如果上一个参数触发，则要看故障百分比，如果百分比超过阈值[参数]，Hystrix将触发断路器，
     * 使将来几乎所有调用都失败（会让部分调用来进行测试，以检查服务是否恢复）。如果10s内未达到阈值，则重置断路器统计信息。<br>
     * - circuitBreaker.sleepWindowInMilliseconds: 如果上一个参数触发，断路器跳闸，则Hystrix将启动一个新的窗口，
     * 每隔一定时间[参数]调用一次这个“坏”服务，如果成功，则重置断路器统计信息并通过调用，如果失败则进入下一个[参数]时间的步骤。<br>
     * - metrics.rollingStats.timeInMilliseconds: 用于控制Hystrix用来监视服务调用问题的窗口大小，默认10s。<br>
     * - metrics.rollingStats.numBuckets: Hystrix在桶中收集数据，默认10个。即使用10s的窗口，将统计数据收集到长度3s的5个桶中。<br>
     * 使用[fallbackMethod]创建后备策略。<br>
     * 使用[threadPoolKey][threadPoolProperties]创建舱壁模式。<br>
     * - coreSize：定义线程池中线程的最大数量<br>
     * - maxQueueSize：排队进入线程池的队列的大小<br>
     * (3) 断路器原理<br>
     * Hystrix不仅能够长时间运行调用，它还会监控调用失败次数，如果调用失败次数足够多，那么Hystrix会在请求发送到远端资源之前跳闸，<br>
     * 即快速失败来自动阻止未来的调用。快速失败可以防止引用程序等待时间超时，也可以阻止服务客户端频繁调用超过负载而崩溃。<br>
     * 快速失败给了性能下降的系统时间去恢复。<br>
     * https://github.com/Netflix/Hystrix/wiki/Configuration
     * HystrixCommandProperties.java
     */
    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "10")

            },
            fallbackMethod = "callFallbackMethod1",
            threadPoolKey = "findAllPoolKey",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),
                    @HystrixProperty(name = "maxQueueSize", value = "101")
            }
    )
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    }, fallbackMethod = "callFallbackMethod2")
    public Employee findById(String id) {
        Optional<Employee> optResult = employeeRepository.findById(id);
        if (!optResult.isPresent()) {
            throw new AppException(String.format("Can not find the employee which id = %s.", id));
        }
        return optResult.get();
    }

    private List<Employee> callFallbackMethod1() {
        throw new AppException(String.format(ERR1, "EmployeeService.findAll()"));
    }

    private Employee callFallbackMethod2(String id) {
        throw new AppException(String.format(ERR1 + " Parameter is [id=%s].", "EmployeeService.findById(id)", id));
    }
}
