package com.rk;

import com.rk.api.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduledThreadPoolBasedExecutor implements Executor {
    private final static Logger logger = LoggerFactory.getLogger(ScheduledThreadPoolBasedExecutor.class);
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
        long sequenceNumber = counter.incrementAndGet();
        if (sequenceNumber % 100 == 0) logger.debug("Schedule callable #{}", sequenceNumber);

        //calculations with nano-time is just an experiment to check grain of LocalDateTime.now
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
