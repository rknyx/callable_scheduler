# Callable executor sample project

The idea of the project is to implement a service for executing of callable objects with several rules and restrictions:
 - Service should take two arguments: Callable and LocalDateTime
 - Each Callable should be executed at proposed time or nearest future time if service is busy (but not earlier).
 - Callable objects should be executed in order of scheduled time or (if the same time was scheduled) in FIFO order.
 - Callable objects can be scheduled in an arbitrary order and in several threads

## How to use:
Build maven project locally. Add maven dependency to pom.xml:

    <dependency>
        <groupId>com.rk</groupId>
        <artifactId>callable_executor</artifactId>
        <version>1.0</version>
    </dependency>

Import Executor and single correct implementation: SingleThreadScheduledExecutor

    import com.rk.api.Executor;
    import com.rk.SingleThreadScheduledExecutor;
    
Usage:

    executor.schedule(() -> {
                System.out.println("Hello");
                return null;
            },
            LocalDateTime.now().plus(200, ChronoUnit.MILLIS));

## Remarks and limitations:
- Executor operates in a single thread because correct execution order for thread pool is not guaranteed. E.x: two callable with same scheduled time are obtained by two parallel threads. Distribution of tasks between threads is performed in correct FIFO order, however code inside Callable can be started and ended in arbitrary order due to  asynchronous nature of the threads.
- Project contains test ExecutorTest. There are commented implementation of Executor: ScheduledThreadPoolBasedExecutor. While it is a great example of multithreaded scheduler using leader-follower pattern it doesn't guarantee such strict execution order for tasks with close scheduled times. You can uncomment the ScheduledThreadPoolBasedExecutor and check that test will fail.
- A lot of threads can schedule tasks in the same time so tasks are serialized by AtomicLong counter inside service implementation. 
- ArrayBlockingQueue does not have infinite size and should fit into a memory. Content is not swapped to the persistent storage.
- Don't perform thread-operations inside a Callable, like sleep() or interrupt(). Executor can stuck or terminate unexpectedly.