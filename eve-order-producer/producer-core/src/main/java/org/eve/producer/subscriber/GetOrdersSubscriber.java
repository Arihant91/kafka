package org.eve.producer.subscriber;


import org.eve.producer.domain.Order;
import org.eve.producer.domain.OrdersStatsByIdInRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GetOrdersSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(GetOrdersSubscriber.class);

    @Autowired
    private MessageChannel sendProcessedOrdersToKafkaChannel;


    public void processOrders(Message<List<Order>> ordersMessage) {
        sendMessage(ordersMessage.getPayload());
    }

    private void sendMessage(List<Order> orders) {
        Map<Boolean, List<Order>> groupedByOrder = orders.stream().collect(Collectors.partitioningBy(Order::getIsBuyOrder));
        groupedByOrder.values().forEach(orderList -> {
            if (!orderList.isEmpty()) {
                sendProcessedOrdersToKafkaChannel.send(MessageBuilder.withPayload(getOrdersStatsByIdInRegion(orderList)).build());
            }
        });
    }

    private OrdersStatsByIdInRegion getOrdersStatsByIdInRegion(List<Order> orderList) {
        return OrdersStatsByIdInRegion.builder()
                .regionId(orderList.get(0).getRegionId())
                .timeOfScraping(LocalDateTime.now())
                .typeId(orderList.get(0).getTypeId())
                .isBuyOrders(orderList.get(0).getIsBuyOrder())
                .avgPrice(getAvgPrice(orderList))
                .volumeRemain(getSumVolume(orderList))
                .highestPrice(maxPrice(orderList))
                .lowestPrice(getMinPrice(orderList))
                .orderCount(orderList.size())
                .medianPrice(getMedian(orderList))
                .build();
    }

    private BigDecimal getAvgPrice(List<Order> orders) {
        return orders.stream().map(Order::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long getSumVolume(List<Order> orders) {
        return orders.stream().map(Order::getVolumeRemain).mapToLong(Long::longValue).sum();
    }

    private BigDecimal maxPrice(List<Order> orders) {
        return orders.stream().map(Order::getPrice).max(BigDecimal::compareTo).orElse(BigDecimal.valueOf(-1));
    }

    private BigDecimal getMinPrice(List<Order> orders) {
        return orders.stream().map(Order::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.valueOf(-1));
    }


    private BigDecimal getMedian(List<Order> orders) {
        orders.sort(Comparator.comparing(Order::getPrice));
        return orders.size() % 2 == 0 ? getMedianWhenEven(orders) : getMedianWhenOdd(orders);
    }

    private BigDecimal getMedianWhenEven(List<Order> orders) {
        int ordersListSize = orders.size();
        BigDecimal upperMed = orders.get(ordersListSize / 2).getPrice();
        BigDecimal lowerMed = orders.get((ordersListSize / 2) - 1).getPrice();
        return upperMed.add(lowerMed).divide(BigDecimal.valueOf(2), RoundingMode.DOWN);
    }

    private BigDecimal getMedianWhenOdd(List<Order> orders) {
        int ordersListSize = orders.size();
        return orders.get((ordersListSize + 1) / 2 - 1).getPrice();
    }


}


