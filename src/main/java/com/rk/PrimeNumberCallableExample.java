package com.rk;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

class PrimeNumberCallableExample implements Callable<Boolean> {
    private final long prNumber;
    private LocalDateTime callLocalDateTime;
    private boolean isCalled = false;
    private long sequenceNumber = -1;

    public PrimeNumberCallableExample(long number) {
        this.prNumber = number;
    }

    public Boolean call() {
        callLocalDateTime = LocalDateTime.now();
        isCalled = true;
        return isPrime(this.prNumber);
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean isCalled() {
        return isCalled;
    }

    public LocalDateTime getCallLocalDateTime() {
        return callLocalDateTime;
    }

    private boolean isPrime(long n) {
        if (n % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }
}
