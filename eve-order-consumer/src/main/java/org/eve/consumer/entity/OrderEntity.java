package org.eve.consumer.entity;

import lombok.Builder;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Table("orders")
public record OrderEntity(
        @PrimaryKeyColumn(name = "region_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        Long regionId,

        @PrimaryKeyColumn(name = "location_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
        Long locationId,

        @PrimaryKeyColumn(name = "type_id", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
        Long typeId,

        @PrimaryKeyColumn(name = "time_of_scraping", ordinal = 3, type = PrimaryKeyType.CLUSTERED )
        LocalDateTime timeOfScraping,

        @PrimaryKeyColumn(name = "order_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
        Long orderId,

        @Column("duration")
        Integer duration,

        @Indexed
        @Column("is_buy_order")
        Boolean isBuyOrder,

        @Column("issued")
        String issued,

        @Column("min_volume")
        Integer minVolume,

        @Column("price")
        BigDecimal price,

        @Column("range")
        String range,

        @Column("system_id")
        Long systemId,

        @Column("volume_remain")
        Long volumeRemain,

        @Column("volume_total")
        Long volumeTotal
) {}