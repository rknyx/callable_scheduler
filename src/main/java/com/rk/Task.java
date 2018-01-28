package com.rk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

class Task implements Runnable, Comparable<Task> {
    private final static Logger logger = LoggerFactory.getLogger(Task.class);
    private final Callable callable;
    private final LocalDateTime localDateTime;
    private final long sequenceNumber;

    Task(Callable callable, LocalDateTime localDateTime, long sequenceNumber) {
        this.callable = callable;
        this.localDateTime = localDateTime;
        this.sequenceNumber = sequenceNumber;

        //just for testing purposes
        if (callable instanceof PrimeNumberCallableExample) {
            ((PrimeNumberCallableExample) callable).setSequenceNumber(sequenceNumber);
        }
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    @Override
    public void run() {
        try {
            if (sequenceNumber % 100 == 0) logger.debug("Task #{} started", sequenceNumber);
            callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compareTo(Task that) {
        int localDateTimeComparisonResult = this.localDateTime.compareTo(that.localDateTime);
        if (localDateTimeComparisonResult == 0) {
            return Long.compare(this.sequenceNumber, that.sequenceNumber);
        }
        return localDateTimeComparisonResult;
    }
}
