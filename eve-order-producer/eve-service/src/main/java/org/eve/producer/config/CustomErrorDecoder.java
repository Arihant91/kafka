package org.eve.producer.config;


import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    public static final int MINUTE = 60000;
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == 420) {
            return new RetryableException(response.status(), "420 error have to chill", response.request().httpMethod(), null, Long.valueOf(MINUTE), response.request());
        }
        if (response.status() == 504) {
            // Handle 504 Gateway Timeout differently if needed
            return new RetryableException(
                    response.status(), "Gateway Timeout, retrying...",
                    response.request().httpMethod(),
                    null,
                    Long.valueOf(MINUTE),
                    response.request()
            );

        }
        if (response.status() == 502) {
            // Handle 504 Gateway Timeout differently if needed
            return new RetryableException(
                    response.status(), "Bad Gateway, retrying...",
                    response.request().httpMethod(),
                    null,
                    Long.valueOf(MINUTE),
                    response.request()
            );

        }
        return defaultDecoder.decode(s, response);
    }
}
