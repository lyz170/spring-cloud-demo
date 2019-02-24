package com.mycloud.demo.hystrix;

import java.util.concurrent.Callable;

import com.mycloud.demo.usercontext.UserContext;
import com.mycloud.demo.usercontext.UserContextHolder;

public final class DelegatingUserContextCallable<V> implements Callable<V> {

    private final Callable<V> delegate;
    private UserContext originalUserContext;

    public DelegatingUserContextCallable(Callable<V> delegate, UserContext userContext) {
        this.delegate = delegate;
        this.originalUserContext = userContext;
    }

    @Override
    public V call() throws Exception {
        UserContextHolder.setContext(originalUserContext);

        try {
            // 调用由@HystrixCommand保护的方法
            return delegate.call();
        } finally {
            this.originalUserContext = null;
        }
    }

    public static <V> Callable<V> create(Callable<V> delegate, UserContext userContext) {
        return new DelegatingUserContextCallable<V>(delegate, userContext);
    }
}
