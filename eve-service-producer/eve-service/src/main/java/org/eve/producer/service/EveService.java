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

    public List<Integer> getRegionIds(){
        return eveClient.getRegions();
    }

    public ResponseEntity<List<Order>> getRegionOrdersByPage(Integer regionId, Integer page){
        return eveClient.getMarketOrdersByRegion(regionId, page);
    }

}