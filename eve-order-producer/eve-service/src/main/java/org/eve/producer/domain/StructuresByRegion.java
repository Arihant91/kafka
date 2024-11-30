package org.eve.producer.domain;

import java.util.List;

public record StructuresByRegion(Long regionId, List<Structures> structures) {
}
