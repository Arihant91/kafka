package org.eve.producer.subscriber;

import org.eve.producer.domain.Ids;
import org.eve.producer.domain.Order;
import org.eve.producer.service.EveService;
import org.eve.producer.service.RateLimiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ProcessOrdersSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(ProcessOrdersSubscriber.class);

    @Autowired
    private MessageChannel getOrdersChannel;

    @Autowired
    private EveService eveService;


    @Autowired
    private RateLimiterService rateLimiterService;

    public void processOrders(Message<Ids> idsMessage) {
        Ids ids = idsMessage.getPayload();
        int page = 1;
        ResponseEntity<List<Order>> response = fetchOrders(ids.regionId(), ids.typeId(), page);
        List<Order> orderList = new ArrayList<>(Objects.requireNonNull(response.getBody()));
        HttpHeaders headers = response.getHeaders();
        rateLimiterService.checkRateLimit(headers);
        int pages = extractTotalPages(headers);
        while (page < pages) {
            page += 1;
            ResponseEntity<List<Order>> pageResponse = eveService.getRegionOrdersByPage(ids.regionId(), ids.typeId(), page);
            rateLimiterService.checkRateLimit(pageResponse.getHeaders());
            orderList.addAll(Objects.requireNonNull(pageResponse.getBody()));
        }
        orderList.forEach(order -> order.setRegionId(ids.regionId()));
        sendMessage(orderList);
    }

    private int extractTotalPages(HttpHeaders headers) {
        String xPages = headers.getFirst("X-Pages");
        if (xPages != null && !xPages.isEmpty()) {
            return Integer.parseInt(xPages);
        }
        return 1;
    }

    private ResponseEntity<List<Order>> fetchOrders(Long regionId, Long typeId, int page) {
        return eveService.getRegionOrdersByPage(regionId, typeId, page);
    }

    private void sendMessage(List<Order> orders) {
        getOrdersChannel.send(MessageBuilder.withPayload(orders).build());
        logger.info("Order sent from ProcessOrdersSubscriber");
    }

}
