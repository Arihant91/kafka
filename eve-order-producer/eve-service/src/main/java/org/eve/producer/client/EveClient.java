package org.eve.producer.client;

import org.eve.producer.domain.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@FeignClient(name = "eveClient", url = "${eve.base-url}")
public interface EveClient {

    @GetMapping("${eve.version}" + "${eve.urls.prices}" + "${eve.datasource}")
    String getPrices();

    @GetMapping("${eve.version}" + "${eve.urls.marketOrdersByRegion}" + "${eve.datasource}")
    ResponseEntity<List<Order>> getMarketOrdersByRegion(@PathVariable("regionId") Long regionId,@RequestParam ("typeId") Long typeId, @RequestParam("page") Integer page);

    @GetMapping("${eve.version}" + "${eve.urls.getRegions}" + "${eve.datasource}")
    List<Long> getRegions();

    @GetMapping("${eve.version}" + "${eve.urls.getTypes}" + "${eve.datasource}")
    ResponseEntity<List<Long>> getTypes(@RequestParam("page") Integer page);

    @GetMapping("${eve.version}" + "${eve.urls.getRelevantTypes}" + "${eve.datasource}")
    ResponseEntity<List<Long>> getRelevantTypesByRegion(@PathVariable("regionId") Long regionId, @RequestParam("page") Integer page);

}