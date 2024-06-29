package org.eve.consumer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(@JsonProperty("region_id") Long regionId,
                    @JsonProperty("location_id") Long locationId,
                    @JsonProperty("type_id") Long typeId,
                    @JsonProperty("order_id") Long orderId,
                    @JsonProperty("duration") Integer duration,
                    @JsonProperty("is_buy_order") Boolean isBuyOrder,
                    @JsonProperty("issued") String issued,

                    @JsonProperty("min_volume") Integer minVolume,

                    @JsonProperty("price") BigDecimal price,
                    @JsonProperty("range") String range,
                    @JsonProperty("system_id") Long systemId,
                    @JsonProperty("volume_remain") Long volumeRemain,
                    @JsonProperty("volume_total") Long volumeTotal,
                    @JsonProperty("time_of_scraping") LocalDateTime timeOfScraping) {
}