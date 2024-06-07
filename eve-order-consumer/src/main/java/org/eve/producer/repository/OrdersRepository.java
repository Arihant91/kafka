package org.eve.producer.repository;

import org.eve.producer.entity.OrderEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface OrdersRepository extends CassandraRepository<OrderEntity, String> {
}