package org.eve.producer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;


public record Order(@JsonProperty("duration")Integer duration,
                    @JsonProperty("is_buy_order") Boolean isBuyOrder,
                    @JsonProperty("issued") String issued,
                    @JsonProperty("location_id")Long locationId,
                    @JsonProperty("min_volume")Integer minVolume,
                    @JsonProperty("order_id")Long orderId,
                    @JsonProperty("price")BigDecimal price,
                    @JsonProperty("range")String range,
                    @JsonProperty("system_id")Long systemId,
                    @JsonProperty("type_id")Long typeId,
                    @JsonProperty("volume_remain")Long volumeRemain,
                    @JsonProperty("volume_total")Long volumeTotal) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;
        return Objects.equals(typeId, order.typeId) && Objects.equals(orderId, order.orderId) && Objects.equals(range, order.range) && Objects.equals(issued, order.issued) && Objects.equals(systemId, order.systemId) && Objects.equals(locationId, order.locationId) && Objects.equals(duration, order.duration) && Objects.equals(price, order.price) && Objects.equals(volumeTotal, order.volumeTotal) && Objects.equals(minVolume, order.minVolume) && Objects.equals(volumeRemain, order.volumeRemain) && Objects.equals(isBuyOrder, order.isBuyOrder);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(duration);
        result = 31 * result + Objects.hashCode(isBuyOrder);
        result = 31 * result + Objects.hashCode(issued);
        result = 31 * result + Objects.hashCode(locationId);
        result = 31 * result + Objects.hashCode(minVolume);
        result = 31 * result + Objects.hashCode(orderId);
        result = 31 * result + Objects.hashCode(price);
        result = 31 * result + Objects.hashCode(range);
        result = 31 * result + Objects.hashCode(systemId);
        result = 31 * result + Objects.hashCode(typeId);
        result = 31 * result + Objects.hashCode(volumeRemain);
        result = 31 * result + Objects.hashCode(volumeTotal);
        return result;
    }
}