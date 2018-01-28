package com.rk;


import com.rk.api.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * Unit test for simple App.
 */
public class ExecutorTest {
    private static final String INCORRECT_ORDER_PATTERN = "\nCallable scheduled to: '%s' was called in: '%s',\n" +
            "Callable scheduled to: '%s' was called in: '%s'";
    private final static Logger logger = LoggerFactory.getLogger(ExecutorTest.class);
    private LocalDateTime now;

    @Test
    public void simpleExecutorTest() throws InterruptedException {
        final int LARGE_ENOUGH_PRIME_NUMBER = 835937831;
        final int RANGE = 5000;
        final int SCHEDULE_TIMEOUT_MILLIS = 10000;
        final int COUNT_OF_REPEATED_TIMES = 2000;
        final int MILLIS_START = 3000;
        final int MILLIS_END = 13000;
        final int MILLIS_STEP = (MILLIS_END - MILLIS_START) / RANGE;
        final int SIMULTANEOUS_SCHEDULING_THREADS = Runtime.getRuntime().availableProcessors() / 2;
        now = LocalDateTime.now();

        //list of sequential tasks
        List<Pair<PrimeNumberCallableExample, LocalDateTime>> sequentialTasks = new ArrayList<>();
        IntStream.range(0, RANGE).forEach(k ->
                sequentialTasks.add(Pair.of(new PrimeNumberCallableExample(LARGE_ENOUGH_PRIME_NUMBER),
                dateTimeWithDelay(MILLIS_START + MILLIS_STEP * k))));

        Collections.shuffle(sequentialTasks);

        //list of simultaneous tasks
        IntStream.range(0, COUNT_OF_REPEATED_TIMES).forEach(k ->
            sequentialTasks.add(Pair.of(new PrimeNumberCallableExample(LARGE_ENOUGH_PRIME_NUMBER),
                    dateTimeWithDelay((MILLIS_END - MILLIS_START) / 2))));

        logger.info("Will solve '{}' prime number tasks.", sequentialTasks.size());
        logger.info("Start scheduling in '{}' threads", SIMULTANEOUS_SCHEDULING_THREADS);
        //schedule, comment or uncomment particular executor to see test result
        Executor testedExecutor = new SingleThreadScheduledExecutor();
//        Executor testedExecutor = new ScheduledThreadPoolBasedExecutor();
        ExecutorService executorService = Executors.newFixedThreadPool(SIMULTANEOUS_SCHEDULING_THREADS);

        sequentialTasks.forEach(pair -> executorService.submit(() -> testedExecutor.schedule(pair.getLeft(), pair.getRight())));
        executorService.awaitTermination(SCHEDULE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        logger.info("Call shutdown");
        testedExecutor.shutdown();
        logger.info("Call awaitTermination");
        testedExecutor.awaitTermination();

        sequentialTasks.forEach(pair ->
            Assert.assertTrue("Unprocessed callable", pair.getLeft().isCalled()));

        sequentialTasks.forEach(pair -> {
            LocalDateTime actualCallTime = pair.getLeft().getCallLocalDateTime();
            LocalDateTime scheduledCallTime = pair.getRight();
            Assert.assertTrue(
                    String.format("Call time is earlier than scheduled. Call:' %s'. Scheduled: '%s'",
                            actualCallTime, scheduledCallTime),
                    actualCallTime.equals(scheduledCallTime) || actualCallTime.isAfter(scheduledCallTime));
        });

        //sort in a required FIFO order
        if (testedExecutor instanceof SingleThreadScheduledExecutor) {
            sequentialTasks.sort((pairA, pairB) -> {
                int datesComparisonResult = pairA.getRight().compareTo(pairB.getRight());
                return datesComparisonResult == 0
                        ? Long.compare(pairA.getLeft().getSequenceNumber(), pairB.getLeft().getSequenceNumber())
                        : datesComparisonResult;
            });
        } else {
            sequentialTasks.sort(Comparator.comparing(Pair::getRight));
        }

        for (int i = 0; i < sequentialTasks.size() - 1; i++) {
            final Pair<PrimeNumberCallableExample, LocalDateTime> prev = sequentialTasks.get(i);
            final Pair<PrimeNumberCallableExample, LocalDateTime> next = sequentialTasks.get(i + 1);
            final String msg = String.format(INCORRECT_ORDER_PATTERN,
                    prev.getRight(), prev.getLeft().getCallLocalDateTime(),
                    next.getRight(), next.getLeft().getCallLocalDateTime());
            Assert.assertTrue(msg, prev.getLeft().getCallLocalDateTime().compareTo(next.getLeft().getCallLocalDateTime()) < 1);
        }

        Executor executor = new SingleThreadScheduledExecutor();
        executor.schedule(() -> {
                    System.out.println("Hello");
                    return null;
                },
                LocalDateTime.now().plus(200, ChronoUnit.MILLIS));

    }

    private LocalDateTime dateTimeWithDelay(long delayMillis) {
        return now.plus(delayMillis, MILLIS);
    }
}
