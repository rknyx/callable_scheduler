package com.rk;

import com.rk.api.Executor;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class SingleThreadScheduledExecutor implements Executor {
    private final PriorityBlockingQueue<Task> queue;
    private Thread executionThread;
    private AtomicLong sequencer = new AtomicLong(0);
    private AtomicBoolean isThreadStarted = new AtomicBoolean(false);
    private volatile boolean isShutdown = false;

    public SingleThreadScheduledExecutor() {
        queue = new PriorityBlockingQueue<>();
        executionThread = new ExecutorThread(queue);
    }

    public void schedule(Callable callable, LocalDateTime localDateTime) {
        if (isThreadStarted.compareAndSet(false, true)) {
            executionThread.start();
        }

        if (isShutdown) {
            return;
        }

        queue.add(new Task(callable, localDateTime, sequencer.getAndIncrement()));
    }

    public void shutdown() {
        isShutdown = true;
        executionThread.interrupt();
    }

    public void awaitTermination() throws InterruptedException {
        executionThread.join();
    }
}
