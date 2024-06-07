package org.eve.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ChannelConfig {

    @Bean
    public MessageChannel getOrdersChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel sendToKafkaChannel() {
        return new DirectChannel();
    }

}
