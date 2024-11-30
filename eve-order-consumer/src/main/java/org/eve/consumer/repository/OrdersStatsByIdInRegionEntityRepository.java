package org.eve.consumer.repository;


import org.eve.consumer.entity.OrdersStatsByIdInRegionEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersStatsByIdInRegionEntityRepository extends CassandraRepository<OrdersStatsByIdInRegionEntity, Long> {
}
