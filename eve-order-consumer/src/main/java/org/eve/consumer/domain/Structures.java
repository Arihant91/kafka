package org.eve.consumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType("structures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Structures {
    private Long id;
    private String name;
}
