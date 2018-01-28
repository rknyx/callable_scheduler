package com.rk.api;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public interface Executor {
    /**
     * Schedule given callable for execution in the proposed localDateTime.
     * Callable is supposed but no must be called in the given time. If executor does not fit into time restriction
     * it will execute callable later but not earlier.
     *
     * Don't perform thread-based operations (like sleep) inside callable to avoid scheduler stuck.
     * @param callable callable to execute.
     * @param localDateTime desired execution time
     */
    void schedule(Callable callable, LocalDateTime localDateTime);

    /**
     * Send a signal to stop scheduling. Calls of schedule will be ignored after shutdown was called.
     * Shutdown terminates working threads so don't reuse executor after shutdown was called.
     */
    void shutdown();

    /**
     * Block caller thread unless all scheduled items are processed.
     * @throws InterruptedException
     */
    void awaitTermination() throws InterruptedException;
}
