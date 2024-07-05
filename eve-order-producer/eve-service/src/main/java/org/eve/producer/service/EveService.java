package org.eve.producer.service;


import org.eve.producer.client.EveClient;
import org.eve.producer.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class EveService {

    private final EveClient eveClient;

    private final RateLimiterService rateLimiterService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    public EveService(EveClient eveClient, RateLimiterService rateLimiterService) {
        this.eveClient = eveClient;
        this.rateLimiterService = rateLimiterService;
    }

    public List<Long> getRegionIds() {
        return eveClient.getRegions();
    }

    public List<Long> getTypeIdsPage(Integer page) {
        return eveClient.getTypes(page).getBody();
    }

    public List<Long> getAllTypeIds() {
        int currentPage = 1;
        ResponseEntity<List<Long>> typeIdsResp = eveClient.getTypes(currentPage);
        List<Long> typeIds = new ArrayList<>(getBodyOrEmpty(typeIdsResp));
        rateLimiterService.checkRateLimit(typeIdsResp.getHeaders());
        int totalPages = extractTotalPages(typeIdsResp.getHeaders());

        for (currentPage = 2; currentPage <= totalPages; currentPage++) {
            rateLimiterService.checkRateLimit(typeIdsResp.getHeaders());
            typeIdsResp = eveClient.getTypes(currentPage);
            typeIds.addAll(getBodyOrEmpty(typeIdsResp));
        }
        return typeIds;
    }

    public ResponseEntity<List<Order>> getRegionOrdersByPage(Long regionId, Long typeId, Integer page) {
        return eveClient.getMarketOrdersByRegion(regionId, typeId, page);
    }

    public List<Order> getAllOrdersInRegionByType(Long regionId, Long typeId) {
        int currentPage = 1;
        ResponseEntity<List<Order>> ordersResp = eveClient.getMarketOrdersByRegion(regionId, typeId, currentPage);
        List<Order> orders = new ArrayList<>(Objects.requireNonNull(ordersResp.getBody()));
        rateLimiterService.checkRateLimit(ordersResp.getHeaders());
        int totalPages = extractTotalPages(ordersResp.getHeaders());

        List<CompletableFuture<List<Order>>> futures = IntStream.rangeClosed(2, totalPages)
                .mapToObj(page -> CompletableFuture.supplyAsync(() -> {
                    rateLimiterService.checkRateLimit(ordersResp.getHeaders());
                    ResponseEntity<List<Order>> pageResp = eveClient.getMarketOrdersByRegion(regionId, typeId, page);
                    return Objects.requireNonNull(pageResp.getBody());
                }, executorService))
                .toList();

        for (CompletableFuture<List<Order>> future : futures) {
            try {
                orders.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        orders.forEach(order -> order.setOrderId(regionId));
        return orders;
    }

    private int extractTotalPages(HttpHeaders headers) {
        String xPages = headers.getFirst("X-Pages");
        return (xPages != null && !xPages.isEmpty()) ? Integer.parseInt(xPages) : 1;
    }

    private List<Long> getBodyOrEmpty(ResponseEntity<List<Long>> response) {
        return response.getBody() != null ? response.getBody() : new ArrayList<>();
    }
}