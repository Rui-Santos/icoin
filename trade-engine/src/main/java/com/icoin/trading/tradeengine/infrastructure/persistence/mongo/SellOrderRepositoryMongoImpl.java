package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
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
@SuppressWarnings("UnusedDeclaration")
public class SellOrderRepositoryMongoImpl implements SellOrderRepositoryMongoCustom {
    private static Logger logger = LoggerFactory.getLogger(SellOrderRepositoryMongoImpl.class);

    private MongoTemplate mongoTemplate;
    private final static BigDecimal EQUAL_VALUE = BigDecimal.valueOf(0.0000000001);

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<SellOrder> findAscPendingOrdersByPriceTime(Date toTime,
                                                           BigDecimal price,
                                                           OrderBookId orderBookId,
                                                           int size) {
        notNull(toTime);
        notNull(price);
        notNull(orderBookId);
        hasLength(orderBookId.toString());
        isTrue(size > 0);

        if (logger.isDebugEnabled()) {
            logger.debug("Querying pending sell orders with toDate:{}, price:{}, order book id:{}, size:{}",
                    toTime, price, orderBookId, size);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                //perform a '<=' function
                .addCriteria(Criteria.where("itemPrice").lte(price.multiply(AbstractOrder.SCAL).longValue()))
                .addCriteria(Criteria.where("placeDate").lte(toTime))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.SELL))
                .with(new Sort(Sort.Direction.ASC, "itemPrice", "placeDate").and(new Sort(Sort.Direction.DESC, "itemRemaining")))
                .limit(size);

        //.with(new Sort(Sort.Direction.DESC, "itemPrice", "placeDate").and(new Sort(Sort.Direction.ASC, "placeDate")))
        final List<SellOrder> sellOrders = mongoTemplate.find(query, SellOrder.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Sell order Queried with {} : {}", query, sellOrders);
        }
        return sellOrders;
    }

    @Override
    public SellOrder findPendingOrder(String id) {
        hasLength(id);

        if (logger.isDebugEnabled()) {
            logger.debug("Querying pending sell order with id:{}", id);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("primaryKey").is(id))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.SELL))
                .limit(1);

        final SellOrder order = mongoTemplate.findOne(query, SellOrder.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Queried with {} : {}", query, order);
        }
        return order;
    }

    @Override
    public SellOrder findLowestPricePendingOrder(OrderBookId orderBookId) {
        notNull(orderBookId);
        hasLength(orderBookId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("Lowest sell Price for OrderBook {}", orderBookId);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.SELL))
                .with(new Sort(Sort.Direction.ASC, "itemPrice"))
                .limit(1);

        final SellOrder order = mongoTemplate.findOne(query, SellOrder.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Lowest queried with {} : {}", query, order);
        }
        return order;
    }
}
