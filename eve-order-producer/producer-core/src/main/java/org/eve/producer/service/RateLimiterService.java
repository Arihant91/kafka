package org.eve.producer.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RateLimiterService {

    private static final int ERROR_LIMIT_THRESHOLD = 20;
    private long errorLimitResetTime;

    public void checkRateLimit(HttpHeaders headers) {
        int remainingErrorLimit = Integer.parseInt(Objects.requireNonNull(headers.get("X-ESI-Error-Limit-Remain")).get(0));
        if (remainingErrorLimit < ERROR_LIMIT_THRESHOLD) {
            errorLimitResetTime = Long.parseLong(Objects.requireNonNull(headers.get("X-ESI-Error-Limit-Reset")).get(0));
            try {
                System.out.println("Rate limit reached. Pausing requests.");
                Thread.sleep((errorLimitResetTime + 1) * 1000);
            } catch (InterruptedException e) {
                System.out.println(e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
