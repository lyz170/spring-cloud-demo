package com.mycloud.demo.hystrix;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mycloud.demo.usercontext.UserContextHolder;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

/**
 * 默认情况下（THREAD隔离级别），Hystrix不会将父线程的上下文传播到由Hystrix命令管理的线程中。例如、父线程中设置的ThreadLocal的值，在由@HystrixCommand保护的方法中都是不可用的。
 * Hystrix允许开发人员定义一种自定义的并发策略，它将包装Hystrix调用，并允许开发人员将附加的父线程上下文注入由Hystrix命令管理的线程中。
 */
public class ThreadLocalAwareStrategy extends HystrixConcurrencyStrategy {

    private HystrixConcurrencyStrategy existingConcurrencyStrategy;

    public ThreadLocalAwareStrategy(HystrixConcurrencyStrategy existingConcurrencyStrategy) {
        this.existingConcurrencyStrategy = existingConcurrencyStrategy;
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return existingConcurrencyStrategy != null ? existingConcurrencyStrategy.getBlockingQueue(maxQueueSize)
                : super.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
        return existingConcurrencyStrategy != null ? existingConcurrencyStrategy.getRequestVariable(rv)
                : super.getRequestVariable(rv);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize,
            HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
        return existingConcurrencyStrategy != null
                ? existingConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime,
                        unit, workQueue)
                : super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        // 传递了一个Callable实现DelegatingUserContextCallable，用来将UserContext从执行用户REST服务调用的父线程，设置为保护正在进行工作的方法的Hystrix命令线程。
        // 这个Callable类由Hystrix命令池管理的线程调用。
        return existingConcurrencyStrategy != null
                ? existingConcurrencyStrategy
                        .wrapCallable(new DelegatingUserContextCallable<>(callable, UserContextHolder.getContext()))
                : super.wrapCallable(new DelegatingUserContextCallable<>(callable, UserContextHolder.getContext()));
    }
}
