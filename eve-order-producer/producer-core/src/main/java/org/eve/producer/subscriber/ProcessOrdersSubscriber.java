package org.eve.producer.subscriber;

import org.eve.producer.domain.*;
import org.eve.producer.service.EveService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProcessOrdersSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(ProcessOrdersSubscriber.class);

    @Autowired
    private MessageChannel sendSortedOrdersByRegionChannel;
    @Autowired
    private MessageChannel sendSortedOrdersByLocationChannel;

    @Autowired
    private MessageChannel processStructuresByRegionChannel;

    @Autowired
    private EveService eveService;

    public void processOrders(RegionIdWithOrders regionIdWithOrders) {
        regionIdWithOrders.orderList().forEach(order -> order.setRegionId(regionIdWithOrders.regionId()));
        Map<Boolean, List<Order>> groupedByOrder = regionIdWithOrders.orderList().stream().collect(Collectors.partitioningBy(Order::getIsBuyOrder));
        groupedByOrder.values().forEach(orders -> {
            if (!orders.isEmpty()) {
                Map<Long, List<Order>> orderedByTypeId = orderByTypeId(orders);
                List<OrdersStatsByIdInRegion> ordersStatsByIdInRegionList = orderedByTypeId.values().stream().map(this::getOrdersStatsByIdInRegion).toList();
                sendMessageByRegion(ordersStatsByIdInRegionList);
            }
        });
        Map<Long, List<Order>> orderedByStructure =  regionIdWithOrders.orderList().stream().collect(Collectors.groupingBy(Order::getLocationId));
        orderedByStructure.keySet().forEach( key -> {
            Map<Boolean, List<Order>> structureOrdersGroupedByOrder = orderedByStructure.get(key).stream().collect(Collectors.partitioningBy(Order::getIsBuyOrder));
            structureOrdersGroupedByOrder.values().forEach( orders -> {
                if(!orders.isEmpty()) {
                    Map<Long, List<Order>> orderedByTypeId = orderByTypeId(orders);
                    List<OrdersStatByIdInLocation> ordersStatsByIdInStructureList = orderedByTypeId.values().stream().map(this::getOrdersStatsByIdInLocation).toList();
                    sendMessageByLocation(ordersStatsByIdInStructureList);
                }
            });
        });
        List<Long> structureIds = regionIdWithOrders.orderList().stream().map(Order::getLocationId).distinct().toList();
         if(!structureIds.isEmpty()){
             HashMap<Long, List<Long>> structuresByRegionId = new HashMap<>();
             structuresByRegionId.put(regionIdWithOrders.regionId(), structureIds);
             sendToProcessStructures(structuresByRegionId);
         }
    }

    private  void sendToProcessStructures(HashMap<Long, List<Long>> structuresByRegionId){
        processStructuresByRegionChannel.send(MessageBuilder.withPayload(structuresByRegionId).build());
    }

    private void discrepancyCheck(RegionIdWithOrders regionIdWithOrders) {
        Set<Long> types = new HashSet<>(regionIdWithOrders.orderList().stream().map(Order::getTypeId).toList());
        List<Long> idTypes = eveService.getRelevantTypesByRegion(regionIdWithOrders.regionId());
        List<Long> missing = new ArrayList<>();
        idTypes.stream().forEach(id ->{
            if(!types.contains(id)){
                missing.add(id);
            }
        });
    }

    private OrdersStatByIdInLocation getOrdersStatsByIdInLocation(List<Order> orders){
        return OrdersStatByIdInLocation
                .builder()
                .regionId(orders.getFirst().getRegionId())
                .locationId(orders.getFirst().getLocationId())
                .typeId(orders.getFirst().getTypeId())
                .timeOfScraping(LocalDateTime.now())
                .isBuyOrders(orders.getFirst().getIsBuyOrder())
                .avgPrice(getAvgPrice(orders))
                .volumeRemain(getSumVolume(orders))
                .highestPrice(getMaxPrice(orders))
                .lowestPrice(getMinPrice(orders))
                .orderCount(orders.size())
                .medianPrice(getMedian(orders))
                .stdDeviation(getDeviation(orders))
                .build();
    }

    @NotNull
    private static Map<Long, List<Order>> orderByTypeId(List<Order> orders) {
        return orders.parallelStream().collect(Collectors.groupingBy(Order::getTypeId));
    }

    private OrdersStatsByIdInRegion getOrdersStatsByIdInRegion(List<Order> orderList) {
        return OrdersStatsByIdInRegion
                .builder()
                .regionId(orderList.get(0).getRegionId())
                .timeOfScraping(LocalDateTime.now())
                .typeId(orderList.get(0).getTypeId())
                .isBuyOrders(orderList.get(0).getIsBuyOrder())
                .avgPrice(getAvgPrice(orderList))
                .volumeRemain(getSumVolume(orderList))
                .highestPrice(getMaxPrice(orderList))
                .lowestPrice(getMinPrice(orderList))
                .orderCount(orderList.size())
                .medianPrice(getMedian(orderList))
                .stdDeviation(getDeviation(orderList))
                .build();
    }

    private BigDecimal getDeviation(List<Order> orderList){
        List<BigDecimal>  priceList = orderList.stream().map(Order::getPrice).toList();
        BigDecimal mean = getAvgPrice(orderList);
        return priceList
                .stream()
                .map( price -> price.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(priceList.size()), RoundingMode.HALF_UP)
                .sqrt(new MathContext(2));
    }
    private BigDecimal getAvgPrice(List<Order> orders) {
        return orders.stream().map(Order::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(orders.size()), RoundingMode.HALF_UP);
    }

    private Long getSumVolume(List<Order> orders) {
        return orders.stream().map(Order::getVolumeRemain).mapToLong(Long::longValue).sum();
    }

    private BigDecimal getMaxPrice(List<Order> orders) {
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

    private void sendMessageByRegion(List<OrdersStatsByIdInRegion> ordersStatsByIdInRegionList){
        ordersStatsByIdInRegionList.forEach(order -> {
            sendSortedOrdersByRegionChannel.send(MessageBuilder.withPayload(order).build());
        });

    }

    private void sendMessageByLocation(List<OrdersStatByIdInLocation> ordersStatByIdInLocation){
        ordersStatByIdInLocation.forEach(order -> {
            sendSortedOrdersByLocationChannel.send(MessageBuilder.withPayload(order).build());
        });

    }
}
