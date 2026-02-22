package com.read.consumer.config;

import lombok.AllArgsConstructor;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;

import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class ExponentialBackOffWithJitter implements BackOff {

    private final long initialInterval;
    private final double multiplier;
    private final long maxInterval;
    private final int maxRetries;

    @Override
    public BackOffExecution start() {
        return new BackOffExecution() {

            private int currentAttempt = 0;
            private long currentInterval = initialInterval;

            @Override
            public long nextBackOff() {

                if (currentAttempt >= maxRetries) {
                    return STOP;
                }

                long jitter = ThreadLocalRandom.current()
                        .nextLong(0, 500);

                long delay = Math.min(currentInterval, maxInterval) + jitter;

                currentInterval *= (long) multiplier;
                currentAttempt++;

                return delay;
            }
        };
    }
}