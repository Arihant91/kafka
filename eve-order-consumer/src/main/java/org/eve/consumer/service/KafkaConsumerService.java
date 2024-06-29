package org.eve.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eve.consumer.domain.Order;
import org.eve.consumer.domain.OrdersMean;
import org.eve.consumer.entity.OrderEntity;
import org.eve.consumer.entity.OrdersMeanEntity;
import org.eve.consumer.repository.OrdersMeanRepository;
import org.eve.consumer.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final OrdersRepository ordersRepository;

    private final OrdersMeanRepository ordersMeanRepository;
    @Autowired
    public KafkaConsumerService(OrdersRepository ordersRepository, OrdersMeanRepository ordersMeanRepository){
        this.ordersMeanRepository = ordersMeanRepository;
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
            System.err.println("Failed to deserialize message: " + e.getMessage());
        }

    }

    @KafkaListener(topics = "ordersMean", groupId = "ordersMean", containerFactory = "kafkaListenerContainerFactoryOrderMean")
    public void listen(OrdersMean ordersMean, Acknowledgment ack){
        try{
            OrdersMeanEntity ordersMeanEntity = OrdersMeanEntity
                    .builder()
                    .regionId(ordersMean.getRegionId())
                    .locationId(ordersMean.getLocationId())
                    .typeId(ordersMean.getTypeId())
                    .isBuyOrders(ordersMean.getIsBuyOrders())
                    .timeOfScraping(ordersMean.getTimeOfScraping())
                    .volumeRemain(ordersMean.getVolumeRemain())
                    .avgPrice(ordersMean.getAvgPrice())
                    .highestPrice(ordersMean.getHighestPrice())
                    .lowestPrice(ordersMean.getLowestPrice())
                    .orderCount(ordersMean.getOrderCount())
                    .build();
            ordersMeanRepository.save(ordersMeanEntity);
            System.out.println("ordersMeanEntity entity saved: " +ordersMeanEntity);
            ack.acknowledge();
        } catch (Exception e) {
            System.err.println("Failed to deserialize message: " + e.getMessage());
        }

    }

}