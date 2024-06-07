package org.eve.producer.service;

import org.eve.producer.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersService {

    private final EveService eveService;
    private final MessageChannel getOrdersChannel;

    public OrdersService(EveService eveService, MessageChannel getOrdersChannel){
        this.eveService = eveService;
        this.getOrdersChannel = getOrdersChannel;
    }
    @Scheduled(fixedRate = 60000)
    public void getOrders(){
        eveService.getRegionIds().forEach(this::processOrdersForRegion);
    }

    private void processOrdersForRegion(Integer regionId) {
        int page = 1;
        ResponseEntity<List<Order>> response = fetchOrders(regionId, page);
        sendMessage(response.getBody());
        int pages = extractTotalPages(response);
        while(page < pages){
            page += 1;
            sendMessage(eveService.getRegionOrdersByPage(regionId, page).getBody());
            break;
        }
    }

    private int extractTotalPages(ResponseEntity<List<Order>> response) {
        List<String> headerValues = response.getHeaders().get("X-Pages");
        if (headerValues != null && !headerValues.isEmpty()) {
            return Integer.parseInt(headerValues.get(0));
        }
        return 1;
    }

    private ResponseEntity<List<Order>> fetchOrders(Integer regionId, int page) {
        return eveService.getRegionOrdersByPage(regionId, page);
    }

    private void sendMessage(List<Order> orders){
        getOrdersChannel.send(MessageBuilder.withPayload(orders).build());
    }
}
