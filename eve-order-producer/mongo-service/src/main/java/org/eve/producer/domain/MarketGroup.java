package org.eve.producer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MarketGroup(@JsonProperty("id") String id,
                          @JsonProperty("description") String description,
                          @JsonProperty("market_group_id") Integer marketGroupId,
                          @JsonProperty("parent_group_id") Integer parentGroupId,
                          @JsonProperty("name") String name,
                          @JsonProperty("child_groups") List<MarketGroup> marketGroups,
                          @JsonProperty("types") List<ItemType> itemTypes) {
}
