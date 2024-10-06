package org.eve.consumer.service;

import org.eve.consumer.domain.Order;
import org.eve.consumer.domain.OrdersStatsByIdInRegion;
import org.eve.consumer.entity.OrderEntity;

import org.eve.consumer.entity.OrdersStatsByIdInRegionEntity;
import org.eve.consumer.repository.OrdersStatsByIdInRegionEntityRepository;
import org.eve.consumer.repository.OrdersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final OrdersRepository ordersRepository;

    private final OrdersStatsByIdInRegionEntityRepository ordersStatsByIdInRegionEntityRepository;

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    @Autowired
    public KafkaConsumerService(OrdersRepository ordersRepository, OrdersStatsByIdInRegionEntityRepository ordersStatsByIdInRegionEntityRepository){
        this.ordersStatsByIdInRegionEntityRepository = ordersStatsByIdInRegionEntityRepository;
        this.ordersRepository = ordersRepository;
    }

    @KafkaListener(topics = "orders", groupId = "orders", containerFactory = "kafkaListenerContainerFactoryOrder")
    public void listen(Order order, Acknowledgment ack) {
        try{
            OrderEntity orderEntity = OrderEntity.builder()
                    .regionId(order.regionId())
                    .duration(order.duration())
                    .isBuyOrder(order.isBuyOrder())
                    .issued(order.issued())
                    .locationId(order.locationId())
                    .minVolume(order.minVolume())
                    .orderId(order.orderId())
                    .price(order.price())
                    .range(order.range())
                    .systemId(order.systemId())
                    .typeId(order.typeId())
                    .volumeRemain(order.volumeRemain())
                    .volumeTotal(order.volumeTotal())
                    .timeOfScraping(order.timeOfScraping())
                    .build();
            ordersRepository.save(orderEntity);
            ack.acknowledge();
        } catch (Exception e) {
            logger.info("Failed to deserialize message: {}", e.getMessage());
        }

    }

    @KafkaListener(topics = "ordersMean", groupId = "ordersMean", containerFactory = "kafkaListenerContainerFactoryOrderMean")
    public void listen(OrdersStatsByIdInRegion ordersMean, Acknowledgment ack){
        try{
            OrdersStatsByIdInRegionEntity ordersStatsByIdInRegionEntity = OrdersStatsByIdInRegionEntity
                    .builder()
                    .regionId(ordersMean.getRegionId())
                    .typeId(ordersMean.getTypeId())
                    .isBuyOrders(ordersMean.getIsBuyOrders())
                    .timeOfScraping(ordersMean.getTimeOfScraping())
                    .volumeRemain(ordersMean.getVolumeRemain())
                    .avgPrice(ordersMean.getAvgPrice())
                    .medianPrice(ordersMean.getMedianPrice())
                    .highestPrice(ordersMean.getHighestPrice())
                    .lowestPrice(ordersMean.getLowestPrice())
                    .orderCount(ordersMean.getOrderCount())
                    .build();
            ordersStatsByIdInRegionEntityRepository.save(ordersStatsByIdInRegionEntity);
            logger.info("ordersMeanEntity entity saved: {}", ordersStatsByIdInRegionEntity);
            ack.acknowledge();
        } catch (Exception e) {
            logger.info("Failed to deserialize message: {}", e.getMessage());
        }
    }

}