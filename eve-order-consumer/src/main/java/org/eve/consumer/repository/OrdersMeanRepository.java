package org.eve.consumer.repository;

import org.eve.consumer.entity.OrdersMeanEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface OrdersMeanRepository extends CassandraRepository<OrdersMeanEntity, Long> {
}
