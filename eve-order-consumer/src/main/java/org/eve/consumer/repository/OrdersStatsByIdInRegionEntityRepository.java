package org.eve.consumer.repository;


import org.eve.consumer.entity.OrdersStatsByIdInRegionEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface OrdersStatsByIdInRegionEntityRepository extends CassandraRepository<OrdersStatsByIdInRegionEntity, Long> {
}
