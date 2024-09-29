package org.eve.producer.config;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientConfiguration {

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, 2000, 10);
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(10L, TimeUnit.SECONDS, 60L, TimeUnit.SECONDS, true);
    }
}
