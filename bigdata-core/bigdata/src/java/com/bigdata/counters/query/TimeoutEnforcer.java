package com.bigdata.counters.query;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class TimeoutEnforcer {
    private static final ExecutorService exec = Executors.newCachedThreadPool();

    public static <T> T call(Callable<T> callable) {
        try {
            return exec.submit(callable).get(60, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void call(Runnable runnable) {
        try {
            exec.submit(runnable).get(60, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}