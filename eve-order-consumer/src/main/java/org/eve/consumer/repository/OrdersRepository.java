package org.eve.consumer.repository;

import org.eve.consumer.entity.OrderEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface OrdersRepository extends CassandraRepository<OrderEntity, Long> {
}