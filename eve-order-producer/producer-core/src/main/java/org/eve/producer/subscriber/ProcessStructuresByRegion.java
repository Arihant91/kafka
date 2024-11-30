package org.eve.producer.subscriber;

import org.eve.producer.domain.Structures;
import org.eve.producer.domain.StructuresByRegion;
import org.eve.producer.service.EveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ProcessStructuresByRegion {

    @Autowired
    private EveService eveService;

    @Autowired
    private MessageChannel sendStructuresByRegionChannel;


    public void processStructures(HashMap<Long, List<Long>> structuresByRegionId) {
        if (structuresByRegionId.keySet().stream().findFirst().isPresent()) {
            List<Integer> ids = new ArrayList<>();
            structuresByRegionId.values().stream().flatMap(List::stream).forEach(id -> {
                if (id <= Integer.MAX_VALUE) {
                    ids.add(id.intValue());
                }
            });
            if(!ids.isEmpty()){
                List<Structures> structures = eveService.getStructures(ids);
                Long id = structuresByRegionId.keySet().stream().findFirst().get();
                sendMessage(new StructuresByRegion(id, structures));
            }
        }
    }

    private void sendMessage(StructuresByRegion structuresByRegion) {
        sendStructuresByRegionChannel.send(MessageBuilder.withPayload(structuresByRegion).build());
    }

}
