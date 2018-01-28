package com.rk;

import com.rk.api.Executor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduledThreadPoolBasedExecutor implements Executor {
    private ScheduledExecutorService scheduledExecutorService;
    private AtomicLong counter = new AtomicLong(0);
    private LocalDateTime dateTimeReferencePoint;
    private long nanoTimeReferencePoint;


    public ScheduledThreadPoolBasedExecutor() {
        scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() / 2);
        nanoTimeReferencePoint = System.nanoTime();
        dateTimeReferencePoint = LocalDateTime.now();
    }

    @Override
    public void schedule(Callable callable, LocalDateTime localDateTime) {
        long value = counter.incrementAndGet();
        if (value % 100 == 0) {
            System.out.println(String.format("Schedule callable #%s", value));
        }
        scheduledExecutorService.schedule(callable,
                ChronoUnit.NANOS.between(dateTimeReferencePoint, localDateTime) - (nanoTimeReferencePoint - System.nanoTime()),
                TimeUnit.NANOSECONDS);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        scheduledExecutorService.awaitTermination(60, TimeUnit.SECONDS);
    }
}
