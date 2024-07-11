package org.eve.producer.service;

import org.eve.producer.domain.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);

    private static final Logger fileLogger = LoggerFactory.getLogger("FILE_LOGGER");
    private final EveService eveService;

    private final MongoService mongoService;
    private final MessageChannel processOrdersChannel;

    private List<Long> regionsIds = new ArrayList<>();
    private List<Long> typeIds = new ArrayList<>();

    @Autowired
    public OrdersService(EveService eveService,MongoService mongoService, MessageChannel processOrdersChannel) {
        this.eveService = eveService;
        this.mongoService = mongoService;
        this.processOrdersChannel = processOrdersChannel;
    }

    @Scheduled(cron = "0 * * * * *")
    public void getOrders() {
        Instant startTime = Instant.now();
        AtomicInteger counter = new AtomicInteger();
        if (regionsIds.isEmpty()) {
            regionsIds = eveService.getRegionIds();
        }
        if (typeIds.isEmpty()) {
            typeIds = eveService.getAllTypeIds();
        }
        mongoService.getMarketItemIds(List.of("Ship Equipment"));
//        regionsIds.parallelStream().forEach(regionId ->
//                typeIds.parallelStream().forEach(typeId -> {
//                            counter.getAndIncrement();
//                            logger.info("counter: " + counter);
//                            sendMessage(regionId, typeId);
//                        }
//                )
//        );
        fileLogger.info("Number of regionIds: {} \n Number of typeIds: {} \n number of calls: {}", regionsIds.size(), typeIds.size(), counter.get());
        logger.info("Process took in minutes: {}", Duration.between(Instant.now(), startTime).toMinutes());
    }

    private void sendMessage(Long regionId, Long typeId) {
        processOrdersChannel.send(MessageBuilder.withPayload(new Ids(regionId, typeId)).build());
    }

}
