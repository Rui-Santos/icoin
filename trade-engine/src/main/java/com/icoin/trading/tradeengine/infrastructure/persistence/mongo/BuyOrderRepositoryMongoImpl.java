package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-6
 * Time: PM9:45
 * To change this template use File | Settings | File Templates.
 */
public class BuyOrderRepositoryMongoImpl implements BuyOrderRepositoryMongoCustom {
    private static Logger logger = LoggerFactory.getLogger(BuyOrderRepositoryMongoImpl.class);

    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<BuyOrder> findDescPendingOrdersByPriceTime(Date toTime,
                                                           BigDecimal price,
                                                           OrderBookId orderBookId,
                                                           int size) {
        notNull(toTime);
        notNull(price);
        notNull(orderBookId);
        hasLength(orderBookId.toString());
        isTrue(size > 0);

        if (logger.isDebugEnabled()) {
            logger.debug("Querying pending buy orders with toDate:{}, price:{}, order book id:{}, size:{}",
                    toTime, price, orderBookId, size);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                .addCriteria(Criteria.where("itemPrice").gte(price.multiply(AbstractOrder.SCAL).longValue()))
                .addCriteria(Criteria.where("placeDate").lte(toTime))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.BUY))
                .with(new Sort(Sort.Direction.DESC, "itemPrice")
                        .and(new Sort(Sort.Direction.ASC, "placeDate")).and(new Sort(Sort.Direction.DESC, "itemRemaining")))
                .limit(size);

        //.with(new Sort(Sort.Direction.DESC, "itemPrice", "placeDate").and(new Sort(Sort.Direction.ASC, "placeDate")))
        final List<BuyOrder> buyOrders = mongoTemplate.find(query, BuyOrder.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Buy order Queried with {} : {}", query, buyOrders);
        }
        return buyOrders;
    }



    @Override
    public BuyOrder findHighestPricePendingOrder(OrderBookId orderBookId) {
        notNull(orderBookId);
        hasLength(orderBookId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("Highest buy Price for OrderBook {}", orderBookId);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.BUY))
                .with(new Sort(Sort.Direction.DESC, "itemPrice"))
                .limit(1);

        final BuyOrder order = mongoTemplate.findOne(query, BuyOrder.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Highest buy queried with {} : {}", query, order);
        }
        return order;
    }
}
