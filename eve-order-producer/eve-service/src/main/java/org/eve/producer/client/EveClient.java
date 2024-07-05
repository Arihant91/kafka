package org.eve.producer.client;

import org.eve.producer.domain.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


import java.util.List;

@FeignClient(name = "eveClient", url = "${eve.base-url}")
public interface EveClient {

    @GetMapping("${eve.version}" + "${eve.urls.prices}" + "${eve.datasource}")
    String getPrices();

    @GetMapping("${eve.version}" + "${eve.urls.marketOrdersByRegion}" + "${eve.datasource}&type_id={typeId}")
    ResponseEntity<List<Order>> getMarketOrdersByRegion(@PathVariable("regionId") Long regionId,@PathVariable ("typeId") Long typeId, @RequestHeader("page") Integer page);

    @GetMapping("${eve.version}" + "${eve.urls.getRegions}" + "${eve.datasource}")
    List<Long> getRegions();

    @GetMapping("${eve.version}" + "${eve.urls.getTypes}" + "${eve.datasource}")
    ResponseEntity<List<Long>> getTypes(@RequestHeader("page") Integer page);

}