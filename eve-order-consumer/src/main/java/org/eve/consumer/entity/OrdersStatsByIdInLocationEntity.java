package org.eve.consumer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Table("orders_stats_by_location")
@ToString
@Builder
public class OrdersStatsByIdInLocationEntity {

    @PrimaryKeyColumn(name =  "location_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long locationId;
    @Column("region_id")
    private Long regionId;

    @PrimaryKeyColumn(name = "type_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long typeId;

    @PrimaryKeyColumn(name = "time_of_scraping", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private LocalDateTime timeOfScraping;

    @Column("is_buy_orders")
    private Boolean isBuyOrders;

    @Column("avg_price")
    private BigDecimal avgPrice;

    @Column("median_price")
    private BigDecimal medianPrice;

    @Column("volume_remain")
    private Long volumeRemain;

    @Column("highest_price")
    private BigDecimal highestPrice;

    @Column("lowest_price")
    private BigDecimal lowestPrice;

    @Column("order_count")
    private Integer orderCount;

    @Column("std_deviation")
    private BigDecimal stdDeviation;
}
