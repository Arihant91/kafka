package org.eve.producer.config;

import org.eve.producer.subscriber.GetOrdersSubscriber;
import org.eve.producer.subscriber.SendToKafkaSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.handler.annotation.SendTo;

@Configuration
public class IntegrationFlowConfig {

    @Autowired
    private GetOrdersSubscriber getOrdersSubscriber;

    @Autowired
    private SendToKafkaSubscriber sendToKafkaSubscriber;

    @Bean
    public IntegrationFlow handleGetOrders(){
        return IntegrationFlow.from("getOrdersChannel").handle(getOrdersSubscriber, "processOrders").get();
    }
    @Bean
    public IntegrationFlow handleSendToKafka(){
        return IntegrationFlow.from("sendToKafkaChannel").handle(sendToKafkaSubscriber, "processOrders").get();
    }
}
