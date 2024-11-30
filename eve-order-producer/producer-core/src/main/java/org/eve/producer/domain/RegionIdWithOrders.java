package org.eve.producer.domain;

import java.util.List;

public record RegionIdWithOrders(Long regionId, List<Order> orderList) {
}
