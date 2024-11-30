package org.eve.producer.config;

import org.eve.producer.subscriber.*;
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
    private ProcessStructuresByRegion processStructuresByRegion;

    @Autowired
    private SendToKafkaStructuresByRegion sendToKafkaStructuresByRegion;

    @Autowired
    private SendToKafkaOrdersStatsByIdInRegion sendToKafkaOrdersStatsByIdInRegion;

    @Autowired
    private SendToKafkaOrdersStatsByLocation sendToKafkaOrdersStatsByLocation;



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

    @Bean(name = "processStructuresTaskExecutor")
    public ThreadPoolTaskExecutor processStructuresTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setThreadNamePrefix("processStructuresChannel-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
    @Bean(name = "getOrdersTaskExecutor")
    public ThreadPoolTaskExecutor sendToKafkaExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(50);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(200);
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
    public IntegrationFlow handleSendToKafkaByRegion() {
        return IntegrationFlow.from("sendSortedOrdersByRegionChannel")
                .channel(MessageChannels.executor(sendToKafkaExecutor()))
                .handle(sendToKafkaOrdersStatsByIdInRegion, "processOrdersByRegion")
                .get();
    }

    @Bean
    public IntegrationFlow handleSendToKafkaByLocation() {
        return IntegrationFlow.from("sendSortedOrdersByLocationChannel")
                .channel(MessageChannels.executor(sendToKafkaExecutor()))
                .handle(sendToKafkaOrdersStatsByLocation, "processOrdersByLocation")
                .get();
    }

    @Bean
    public IntegrationFlow handleSendToProcessStructures() {
        return IntegrationFlow.from("processStructuresByRegionChannel")
                .channel(MessageChannels.executor(processStructuresTaskExecutor()))
                .handle(processStructuresByRegion, "processStructures")
                .get();
    }

    @Bean
    public IntegrationFlow handleSendToKafkaByStructures() {
        return IntegrationFlow.from("sendStructuresByRegionChannel")
                .channel(MessageChannels.executor(sendToKafkaExecutor()))
                .handle(sendToKafkaStructuresByRegion, "sendToKafkaStructuresByRegion")
                .get();
    }

}
