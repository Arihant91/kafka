package org.eve.producer.config;

import org.eve.producer.subscriber.GetOrdersSubscriber;
import org.eve.producer.subscriber.ProcessOrdersSubscriber;
import org.eve.producer.subscriber.SendProcessedOrdersToKafkaSubscriber;
import org.eve.producer.subscriber.SendToKafkaSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class IntegrationFlowConfig {

    @Autowired
    private ProcessOrdersSubscriber processOrdersSubscriber;

    @Autowired
    private GetOrdersSubscriber getOrdersSubscriber;

    @Autowired
    private SendToKafkaSubscriber sendOrdersToKafkaSubscriber;

    @Autowired
    private SendProcessedOrdersToKafkaSubscriber sendProcessedOrdersToKafkaSubscriber;

    @Bean(name = "processOrdersTaskExecutor")
    public ThreadPoolTaskExecutor processOrdersTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(100);
        taskExecutor.setMaxPoolSize(200);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setThreadNamePrefix("processOrdersChannel-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
    @Bean(name = "getOrdersTaskExecutor")
    public ThreadPoolTaskExecutor getOrdersTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(40);
        taskExecutor.setQueueCapacity(40);
        taskExecutor.setThreadNamePrefix("getOrdersChannel-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public IntegrationFlow handleProcessOrders(){
        return IntegrationFlow
                .from("processOrdersChannel")
                .channel(MessageChannels.executor(processOrdersTaskExecutor()))
                .handle(processOrdersSubscriber, "processOrders")
                .get();
    }

    @Bean
    public IntegrationFlow handleGetOrders() {
        return IntegrationFlow.from("getOrdersChannel")
                .channel(MessageChannels.executor(getOrdersTaskExecutor()))
                .handle(getOrdersSubscriber, "processOrders")
                .get();
    }

    @Bean
    public IntegrationFlow handleSendToKafka() {
        return IntegrationFlow.from("sendOrdersToKafkaChannel")
                .handle(sendOrdersToKafkaSubscriber, "processOrders")
                .get();
    }

    @Bean
    public IntegrationFlow handleSendProcessedOrdersToKafka() {
        return IntegrationFlow.from("sendProcessedOrdersToKafkaChannel")
                .handle(sendProcessedOrdersToKafkaSubscriber, "processOrders")
                .get();
    }
}
