package org.eve.producer.subscriber;


import org.eve.producer.domain.Order;
import org.eve.producer.domain.OrdersMean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
public class GetOrdersSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(GetOrdersSubscriber.class);

    @Autowired
    private MessageChannel sendOrdersToKafkaChannel;

    @Autowired
    private  MessageChannel sendProcessedOrdersToKafkaChannel;


    public void processOrders(Message<List<Order>> ordersMessage){sendMessage(ordersMessage.getPayload());
    }

    private void sendMessage(List<Order> orders){
        LocalDateTime time = LocalDateTime.now();
        String timeStr = time.format(DateTimeFormatter.ISO_DATE_TIME);
        orders.forEach( order -> {
            order.setTimeOfScraping(time);
            sendOrdersToKafkaChannel.send(MessageBuilder.withPayload(order).build());
        });
        if(!orders.isEmpty()) {
            BigDecimal avgPrice = orders.stream().map(Order::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            Long sumVolume = orders.stream().map(Order::getVolumeRemain).mapToLong(Long::longValue).sum();
            BigDecimal minPrice = orders.stream().map(Order::getPrice).min(BigDecimal::compareTo).get();
            BigDecimal maxPrice = orders.stream().map(Order::getPrice).max(BigDecimal::compareTo).get();
            Integer orderCount = orders.size();

            OrdersMean ordersMean = OrdersMean.builder()
                    .regionId(orders.get(0).getRegionId())
                    .timeOfScraping(time)
                    .locationId(orders.get(0).getLocationId())
                    .typeId(orders.get(0).getTypeId())
                    .isBuyOrders(orders.get(0).getIsBuyOrder())
                    .avgPrice(avgPrice)
                    .volumeRemain(sumVolume)
                    .highestPrice(maxPrice)
                    .lowestPrice(minPrice)
                    .orderCount(orderCount)
                    .build();
            sendProcessedOrdersToKafkaChannel.send(MessageBuilder.withPayload(ordersMean).build());

        }
    }
}


