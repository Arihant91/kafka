package org.eve.producer.service;


import org.aspectj.weaver.ast.Or;
import org.eve.producer.client.EveClient;
import org.eve.producer.domain.Order;
import org.eve.producer.domain.Structures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Service
public class EveService {

    private final EveClient eveClient;

    private final RateLimiterService rateLimiterService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(400);

    @Autowired
    public EveService(EveClient eveClient, RateLimiterService rateLimiterService) {
        this.eveClient = eveClient;
        this.rateLimiterService = rateLimiterService;
    }

    public List<Structures> getStructures(List<Integer> ids){
        return eveClient.getNames(ids).getBody();
    }

    public List<Long> getRegionIds() {
        return eveClient.getRegions();
    }

    public List<Order> getOrdersByRegion(Long regionId){
        List<Order> ordersList = new ArrayList<>();
        ResponseEntity<List<Order>> resp = eveClient.getMarketOrdersByRegion(regionId, 1);
        ordersList.addAll(Objects.requireNonNull(resp.getBody()));
        rateLimiterService.checkRateLimit(resp.getHeaders());
        int totalPages = extractTotalPages(resp.getHeaders());
        List<CompletableFuture<List<Order>>> futures = IntStream.rangeClosed(2, totalPages)
                .mapToObj(page -> CompletableFuture.supplyAsync(() -> {
                    rateLimiterService.isRateLimitExceededBlock();
                    ResponseEntity<List<Order>> response =eveClient.getMarketOrdersByRegion(regionId, page);
                    rateLimiterService.checkRateLimit(response.getHeaders());
                    return Objects.requireNonNull(response.getBody());
                }, executorService)).toList();

        for (CompletableFuture<List<Order>> future : futures) {
            try {
                ordersList.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return ordersList;
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
            typeIds.addAll(getBodyOrEmpty(eveClient.getTypes(currentPage)));
        }
        return typeIds;
    }

    public List<Long> getRelevantTypesByRegion(Long regionId){
        int currentPage = 1;
        ResponseEntity<List<Long>> typeIdsResp = eveClient.getRelevantTypesByRegion(regionId, currentPage);
        List<Long> typeIds = getBodyOrEmpty(typeIdsResp);
        rateLimiterService.checkRateLimit(typeIdsResp.getHeaders());
        int totalPages = extractTotalPages(typeIdsResp.getHeaders());
        for (currentPage = 2; currentPage <= totalPages; currentPage++) {
            rateLimiterService.checkRateLimit(typeIdsResp.getHeaders());
            typeIds.addAll(getBodyOrEmpty(eveClient.getRelevantTypesByRegion(regionId, currentPage)));
        }
        return typeIds;
    }

    public ResponseEntity<List<Order>> getRegionOrdersByPage(Long regionId, Long typeId, Integer page) {
        return eveClient.getMarketOrdersByIdInRegion(regionId, typeId, page);
    }

    public List<Order> getAllOrdersInRegionByType(Long regionId, Long typeId) {
        int currentPage = 1;

        ResponseEntity<List<Order>> ordersResp = eveClient.getMarketOrdersByIdInRegion(regionId, typeId, currentPage);
        List<Order> orders = new CopyOnWriteArrayList<>(Objects.requireNonNull(ordersResp.getBody()));
        rateLimiterService.checkRateLimit(ordersResp.getHeaders());
        int totalPages = extractTotalPages(ordersResp.getHeaders());

        List<CompletableFuture<List<Order>>> futures = IntStream.rangeClosed(2, totalPages)
                .mapToObj(page -> CompletableFuture.supplyAsync(() -> {
                    rateLimiterService.checkRateLimit(ordersResp.getHeaders());
                    ResponseEntity<List<Order>> pageResp = eveClient.getMarketOrdersByIdInRegion(regionId, typeId, page);
                    return Objects.requireNonNull(pageResp.getBody());
                }, executorService))
                .toList();

        for (CompletableFuture<List<Order>> future : futures) {
            try {
                orders.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        orders.forEach(order -> order.setRegionId(regionId));
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