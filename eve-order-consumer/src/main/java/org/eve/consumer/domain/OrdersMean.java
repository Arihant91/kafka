package org.eve.consumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersMean {
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
}
