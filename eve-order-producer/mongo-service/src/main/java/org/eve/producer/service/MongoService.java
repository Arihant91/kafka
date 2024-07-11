package org.eve.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.eve.producer.domain.ItemType;
import org.eve.producer.domain.MarketGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MongoService {

    public static final String NAME = "name";
    @Value("${mongodb.db}")
    private String dataBase;

    @Value("${mongodb.collection}")
    private String collectionName;

    private final MongoClient mongoClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MongoService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }


    public List<MarketGroup> getMarketItemIds(List<String> groupNames) {
        List<MarketGroup> marketGroupList = new ArrayList<>();
            MongoCollection<Document> collection = mongoClient.getDatabase(dataBase).getCollection(collectionName);
            groupNames.forEach(name -> {
                Document query = new Document(NAME, name);
                Document result = collection.find(query).first();
                if (result != null) {
                    try {
                        MarketGroup marketGroup = objectMapper.readValue(result.toJson(), MarketGroup.class);
                        marketGroupList.add(marketGroup);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        });
        List<Integer> typeIds = new ArrayList<>(marketGroupList.stream().map(this::findIds).flatMap(Collection::stream).toList());
        return marketGroupList;
    }

    private List<Integer> findIds(MarketGroup marketGroup){
        if(marketGroup.marketGroups() != null){
            return marketGroup.marketGroups().stream().flatMap(group -> findIds(group).stream()).toList();
        }
        if(marketGroup.itemTypes() != null){
           return marketGroup.itemTypes().stream().map(ItemType::typeId).toList();
        }
        return new ArrayList<>();
    }


}
