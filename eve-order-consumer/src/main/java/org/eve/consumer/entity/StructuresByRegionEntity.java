package org.eve.consumer.entity;

import lombok.Builder;
import lombok.ToString;
import org.eve.consumer.domain.Structures;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table("structures_by_region")
@ToString
@Builder
public class StructuresByRegionEntity {

    @PrimaryKeyColumn(name = "region_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long regionId;

    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "structures")
    private List<Structures> structures;
}
