package org.eve.producer.service;


import org.eve.producer.client.EveClient;
import org.eve.producer.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EveService {
    private final EveClient eveClient;

    public EveService(@Autowired EveClient eveClient){
        System.out.println("built");
        this.eveClient = eveClient;
    }

    public List<Long> getRegionIds(){
        return eveClient.getRegions();
    }

    public List<Long> getTypeIds(){
        return eveClient.getTypes();
    }

    public ResponseEntity<List<Order>> getRegionOrdersByPage(Long regionId, Long typeId, Integer page){
        return eveClient.getMarketOrdersByRegion(regionId, typeId, page);
    }

}