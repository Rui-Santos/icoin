package com.icoin.axonsupport.infrastructure.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.axonframework.eventstore.mongo.MongoTemplate;
import org.springframework.data.mongodb.MongoDbFactory;

import static com.homhon.util.Asserts.hasLength;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-4
 * Time: PM10:05
 * To change this template use File | Settings | File Templates.
 */
public class EventStoreTemplete implements MongoTemplate {

    private static final String DEFAULT_DOMAINEVENTS_COLLECTION = "domainevents";
    private static final String DEFAULT_SNAPSHOTEVENTS_COLLECTION = "snapshotevents";

    private final String domainEventsCollectionName;
    private final String snapshotEventsCollectionName;

    private final MongoDbFactory mongoDbFactory;

    public EventStoreTemplete(MongoDbFactory mongoDbFactory) {
        this(DEFAULT_DOMAINEVENTS_COLLECTION, DEFAULT_SNAPSHOTEVENTS_COLLECTION, mongoDbFactory);
    }

    public EventStoreTemplete(String domainEventsCollectionName, String snapshotEventsCollectionName, MongoDbFactory mongoDbFactory) {
        hasLength(domainEventsCollectionName);
        hasLength(snapshotEventsCollectionName);

        this.domainEventsCollectionName = domainEventsCollectionName;
        this.snapshotEventsCollectionName = snapshotEventsCollectionName;
        this.mongoDbFactory = mongoDbFactory;
    }

    @Override
    public DBCollection domainEventCollection() {
        return database().getCollection(domainEventsCollectionName);
    }

    @Override
    public DBCollection snapshotEventCollection() {
        return database().getCollection(snapshotEventsCollectionName);
    }

    //    @Override
    public DB database() {
        return mongoDbFactory.getDb();
    }
}