package org.eve.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RateLimiterService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);
    private static final int ERROR_LIMIT_THRESHOLD = 30;
    private final AtomicInteger errorLimitResetTime = new AtomicInteger(0);
    private final AtomicBoolean rateLimitExceeded = new AtomicBoolean(false);
    private int lastRateLimit = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition rateLimitCondition = lock.newCondition();

    public void checkRateLimit(HttpHeaders headers) {
        while (rateLimitExceeded.get()) {
            try {
                logger.info("Waiting for rate limit reset");
                rateLimitCondition.await();
                Thread.sleep(errorLimitResetTime.get() * 1000L);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for rate limit reset", e);
                Thread.currentThread().interrupt();
            }
        }
        lock.lock();
        try {
            String remainingErrorLimitHeader = headers.getFirst("X-ESI-Error-Limit-Remain");
            String errorLimitResetHeader = headers.getFirst("X-ESI-Error-Limit-Reset");

            if (remainingErrorLimitHeader == null || errorLimitResetHeader == null) {
                logger.warn("Missing rate limit headers in response.");
                return;
            }

            int remainingErrorLimit = Integer.parseInt(remainingErrorLimitHeader);
            int resetTime = Integer.parseInt(errorLimitResetHeader);

            if (remainingErrorLimit <= ERROR_LIMIT_THRESHOLD) {
                errorLimitResetTime.set(resetTime);
                rateLimitExceeded.set(true);
                try {
                    logger.info("Rate limit reached. Pausing requests.");
                    logger.info("Remaining rate limit: {}", remainingErrorLimit);
                    logger.info("Request paused till: {} seconds", errorLimitResetTime.get());

                    if (remainingErrorLimit != lastRateLimit) {
                        lastRateLimit = remainingErrorLimit;
                        long sleepTimeInMs = (errorLimitResetTime.get() + 5) * 1000L;
                        Thread.sleep(sleepTimeInMs);
                    }
                } catch (InterruptedException e) {
                    logger.error("Thread interrupted while sleeping for rate limit reset", e);
                    Thread.currentThread().interrupt();
                } finally {
                    rateLimitExceeded.set(false);
                    rateLimitCondition.signalAll();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isRateLimitExceeded(){
        return this.rateLimitExceeded.get();
    }
}
