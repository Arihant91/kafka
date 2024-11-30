package org.eve.consumer.repository;

import org.eve.consumer.entity.OrdersStatsByIdInLocationEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatsByIdInLocationEntityRepository extends CassandraRepository<OrdersStatsByIdInLocationEntity, Long> {
}
