package com.rk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


class ExecutorThread extends Thread {
    private static final long QUEUE_WAIT_TIMEOUT_MILLIS = 100;
    private final BlockingQueue<Task> queue;
    private final static Logger logger = LoggerFactory.getLogger(ExecutorThread.class);

    ExecutorThread(BlockingQueue<Task> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        logger.info("Execution thread started");
        boolean shutdown = false;
        for (;;) {
            try {
                //poll instead of take to avoid eternal wait if thread was interrupted on non-empty queue
                Task task = queue.poll(QUEUE_WAIT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                if (task == null) {
                    if (shutdown || Thread.currentThread().isInterrupted()) {
                        logger.debug("Queue is empty and shutdown was called. Terminating.");
                        break;
                    } else {
                        continue;
                    }
                }
                long remainingMillis = ChronoUnit.MILLIS.between(LocalDateTime.now(), task.getLocalDateTime());
                if (remainingMillis <= 0) {
                    task.run();
                } else {
                    queue.add(task);
                    logger.debug("Time before next callable is {} ms, sleep.", remainingMillis);
                    sleep(remainingMillis);
                }
            } catch (InterruptedException e) {
                if (queue.isEmpty()) {
                    logger.debug("Queue is empty and shutdown was called. Terminating.");
                    break;
                }
                shutdown = true;
            }
        }
        logger.info("Execution thread stopped");
    }
}
