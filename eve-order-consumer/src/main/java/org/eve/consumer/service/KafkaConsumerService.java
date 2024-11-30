package org.eve.consumer.service;

import lombok.RequiredArgsConstructor;
import org.eve.consumer.domain.OrdersStatsByIdInLocation;
import org.eve.consumer.domain.OrdersStatsByIdInRegion;
import org.eve.consumer.domain.StructuresByRegion;

import org.eve.consumer.entity.OrdersStatsByIdInLocationEntity;
import org.eve.consumer.entity.OrdersStatsByIdInRegionEntity;
import org.eve.consumer.entity.StructuresByRegionEntity;
import org.eve.consumer.repository.OrderStatsByIdInLocationEntityRepository;
import org.eve.consumer.repository.OrdersStatsByIdInRegionEntityRepository;
import org.eve.consumer.repository.OrdersRepository;
import org.eve.consumer.repository.StructuresByRegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final OrdersRepository ordersRepository;

    private final OrdersStatsByIdInRegionEntityRepository ordersStatsByIdInRegionEntityRepository;
    private final OrderStatsByIdInLocationEntityRepository ordersStatsByIdInLocationEntityRepository;

    private final StructuresByRegionRepository structuresByRegionRepository;

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "OrdersStatsByIdInLocation", groupId = "OrdersStatsByIdInLocation", containerFactory = "OrdersStatsByIdInLocation")
    public void listen(OrdersStatsByIdInLocation order, Acknowledgment ack) {
        try{
            OrdersStatsByIdInLocationEntity orderEntity = OrdersStatsByIdInLocationEntity.builder()
                    .regionId(order.getRegionId())
                    .locationId(order.getLocationId())
                    .typeId(order.getTypeId())
                    .isBuyOrders(order.getIsBuyOrders())
                    .timeOfScraping(order.getTimeOfScraping())
                    .volumeRemain(order.getVolumeRemain())
                    .avgPrice(order.getAvgPrice())
                    .medianPrice(order.getMedianPrice())
                    .highestPrice(order.getHighestPrice())
                    .lowestPrice(order.getLowestPrice())
                    .orderCount(order.getOrderCount())
                    .stdDeviation(order.getStdDeviation())
                    .build();
            ordersStatsByIdInLocationEntityRepository.save(orderEntity);
            ack.acknowledge();
        } catch (Exception e) {
            logger.info("Failed to deserialize message: {}", e.getMessage());
        }

    }

    @KafkaListener(topics = "OrdersStatsByIdInRegion", groupId = "OrdersStatsByIdInRegion", containerFactory = "OrdersStatsByIdInRegion")
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
                    .stdDeviation(ordersMean.getStdDeviation())
                    .build();
            ordersStatsByIdInRegionEntityRepository.save(ordersStatsByIdInRegionEntity);
            logger.info("orders_stats_by_region entity saved: {}", ordersStatsByIdInRegionEntity);
            ack.acknowledge();
        } catch (Exception e) {
            logger.info("Failed to deserialize message: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "StructuresByRegion", groupId = "StructuresByRegion", containerFactory = "StructuresByRegion")
    public void listen(StructuresByRegion structuresByRegion, Acknowledgment ack){
        try {
            StructuresByRegionEntity structuresByRegionEntity = StructuresByRegionEntity
                    .builder()
                    .regionId(structuresByRegion.getRegionId())
                    .structures(structuresByRegion.getStructures())
                    .build();
            structuresByRegionRepository.save(structuresByRegionEntity);
            logger.info("StructuresByRegion entity saved: {}", structuresByRegionEntity);
            ack.acknowledge();
        } catch (Exception e) {
            logger.info("Failed to deserialize message: {}", e.getMessage());
        }
    }

}