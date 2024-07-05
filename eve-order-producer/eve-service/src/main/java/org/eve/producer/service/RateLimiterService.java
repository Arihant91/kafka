package org.eve.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RateLimiterService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);
    private static final int ERROR_LIMIT_THRESHOLD = 20;
    private long errorLimitResetTime;
    private boolean rateLimitExceeded = false;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition rateLimitCondition = lock.newCondition();

    public void checkRateLimit(HttpHeaders headers) {
        lock.lock();
        try {
            int remainingErrorLimit = Integer.parseInt(Objects.requireNonNull(headers.get("X-ESI-Error-Limit-Remain")).get(0));
            if (remainingErrorLimit < ERROR_LIMIT_THRESHOLD) {
                errorLimitResetTime = Long.parseLong(Objects.requireNonNull(headers.get("X-ESI-Error-Limit-Reset")).get(0));
                rateLimitExceeded = true;
                try {
                    logger.info("Rate limit reached. Pausing requests.");
                    logger.info("Remaining rate limit: {}", remainingErrorLimit);
                    Thread.sleep(errorLimitResetTime * 1000 + 5000);
                } catch (InterruptedException e) {
                    logger.error("Thread interrupted while sleeping for rate limit reset", e);
                    Thread.currentThread().interrupt();
                } finally {
                    rateLimitExceeded = false;
                    rateLimitCondition.signalAll(); // Notify all waiting threads that they can proceed
                }
            } else {
                while (rateLimitExceeded) {
                    try {
                        logger.info("Waiting for rate limit reset");
                        rateLimitCondition.await();
                    } catch (InterruptedException e) {
                        logger.error("Thread interrupted while waiting for rate limit reset", e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
