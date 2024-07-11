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
        if(response.status() == 420){
            return new RetryableException(response.status(), "420 error have to chill", response.request().httpMethod(), null, Long.valueOf(MINUTE), response.request());
        }
        return defaultDecoder.decode(s, response);
    }
}
