package com.icoin.axonsupport.infrastructure.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.axonframework.saga.repository.mongo.MongoTemplate;
import org.springframework.data.mongodb.MongoDbFactory;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-4
 * Time: PM10:04
 * To change this template use File | Settings | File Templates.
 */
public class SagaTemplate implements MongoTemplate {
    static final String SAGA_COLLECTION = "sagacollection";
    private MongoDbFactory mongoDbFactory;

    public SagaTemplate(MongoDbFactory mongoDbFactory) {
        this.mongoDbFactory = mongoDbFactory;
    }

    @Override
    public DBCollection sagaCollection() {
        return database().getCollection(SAGA_COLLECTION);
    }

    private DB database() {
        return mongoDbFactory.getDb();
    }
}
