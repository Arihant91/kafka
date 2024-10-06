package org.eve.producer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrdersStatByIdInLocation {
    private Long regionId;
    private Long locationId;
    private Long typeId;
    private LocalDateTime timeOfScraping;
    private Boolean isBuyOrders;
    private BigDecimal avgPrice;
    private Long volumeRemain;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private Integer orderCount;
    private BigDecimal medianPrice;
}
