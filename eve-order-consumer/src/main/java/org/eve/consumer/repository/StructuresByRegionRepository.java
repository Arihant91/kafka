package org.eve.consumer.repository;

import org.eve.consumer.entity.StructuresByRegionEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StructuresByRegionRepository extends CassandraRepository<StructuresByRegionEntity, Long> {
}
