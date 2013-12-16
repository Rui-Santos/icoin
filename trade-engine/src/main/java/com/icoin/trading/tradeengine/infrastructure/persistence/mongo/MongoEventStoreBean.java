package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters.JodaMoneyConverter;
import com.thoughtworks.xstream.XStream;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.axonframework.eventstore.mongo.MongoTemplate;
import org.axonframework.serializer.xml.XStreamSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-15
 * Time: PM12:08
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
@Configuration
public class MongoEventStoreBean {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean(name="eventStore")
    public MongoEventStore eventStore() {
        XStreamSerializer eventSerializer = new XStreamSerializer();
        XStream xStream = eventSerializer.getXStream();

        xStream.registerConverter(new JodaMoneyConverter());

        MongoEventStore eventStore = new MongoEventStore(eventSerializer, mongoTemplate);
        return eventStore;
    }
}
