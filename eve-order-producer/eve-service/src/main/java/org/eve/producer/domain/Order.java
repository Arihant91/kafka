package org.eve.producer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @JsonProperty("region_id")
    private Long regionId;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("location_id")
    private Long locationId;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("is_buy_order")
    private Boolean isBuyOrder;

    @JsonProperty("issued")
    private String issued;

    @JsonProperty("min_volume")
    private Integer minVolume;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("range")
    private String range;

    @JsonProperty("system_id")
    private Long systemId;

    @JsonProperty("volume_remain")
    private Long volumeRemain;

    @JsonProperty("volume_total")
    private Long volumeTotal;

    @JsonProperty("time_of_scraping")
    private LocalDateTime timeOfScraping;
}