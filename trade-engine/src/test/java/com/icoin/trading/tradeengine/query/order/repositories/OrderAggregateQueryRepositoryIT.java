package com.icoin.trading.tradeengine.query.order.repositories;

import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-10
 * Time: PM11:48
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
public class OrderAggregateQueryRepositoryIT {


    private OrderQueryRepositoryImpl repository = new OrderQueryRepositoryImpl();

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private MongoTemplate mongoTemplate;


    //todo resolve this test method
    @Test
    public void testFindOrderAggregatedPrice() throws Exception {
        repository.setMongoTemplate(mongoTemplate);

        final List<PriceAggregate> orderAggregatedPrice =
                repository.findOrderAggregatedPrice(
                        "0ce4f7bb-592c-4671-8e87-8e691f345bc5",
                OrderType.SELL);

        System.out.println(orderAggregatedPrice);
    }
}
