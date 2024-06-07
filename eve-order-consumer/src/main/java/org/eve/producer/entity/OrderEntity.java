package org.eve.producer.entity;

import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("orders")
public record OrderEntity(
    UUID id,
    Integer duration,
    Boolean isBuyOrder,
    String issued,
    Long locationId,
    Integer minVolume,
    Long orderId,
    BigDecimal price,
    String range,
    Long systemId,
    Long typeId,
    Long volumeRemain,
    Long volumeTotal
){}
