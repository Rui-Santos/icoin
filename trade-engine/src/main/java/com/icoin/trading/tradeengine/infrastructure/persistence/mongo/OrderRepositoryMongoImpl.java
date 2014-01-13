package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/10/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderRepositoryMongoImpl implements OrderRepositoryMongoCustom {
    private static Logger logger = LoggerFactory.getLogger(OrderRepositoryMongoImpl.class);
    private MongoTemplate mongoTemplate;

    @Resource(name = "trade.mongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Order> findAscPendingSellOrdersByPriceTime(Date toTime, BigMoney price, OrderBookId orderBookId, int size) {
        notNull(toTime);
        notNull(price);
        notNull(orderBookId);
        hasLength(orderBookId.toString());
        isTrue(size > 0);

        if (logger.isDebugEnabled()) {
            logger.debug("Querying pending sell orders with toDate:{}, price:{}, order book id:{}, size:{}",
                    toTime, price, orderBookId, size);
        }

        CurrencyUnit unit = price.getCurrencyUnit();

        //init stored long valued money 
        long stored = price.rounded(0, RoundingMode.HALF_EVEN).getAmount().longValue();

        if (unit.getDecimalPlaces() >= 0) {
            BigDecimal scale = BigDecimal.TEN.pow(unit.getDecimalPlaces());
            stored = price.multipliedBy(scale).rounded(0, RoundingMode.HALF_EVEN).getAmount().longValue();
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                .addCriteria(Criteria.where("itemPrice.amount").lte(stored))
                .addCriteria(Criteria.where("itemPrice.currency").is(price.getCurrencyUnit().getCurrencyCode()))
                .addCriteria(Criteria.where("placeDate").lte(toTime))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.SELL))
                .with(new Sort(Sort.Direction.ASC, "itemPrice.amount", "placeDate").and(new Sort(Sort.Direction.DESC, "itemRemaining.amount")))
                .limit(size);

        //.with(new Sort(Sort.Direction.DESC, "itemPrice", "placeDate").and(new Sort(Sort.Direction.ASC, "placeDate"))) 
        final List<Order> sellOrders = mongoTemplate.find(query, Order.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Sell order Queried with {} : {}", query, sellOrders);
        }
        return sellOrders;
    }

    @Override
    public List<Order> findPlacedPendingOrdersAfter(Date toTime, OrderBookId orderBookId, int size) {
        notNull(toTime);
        notNull(orderBookId);
        hasLength(orderBookId.toString());
        isTrue(size > 0);

        if (logger.isDebugEnabled()) {
            logger.debug("Querying ALL pending orders with toDate:{}, order book id:{}, size:{}",
                    toTime, orderBookId, size);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                .addCriteria(Criteria.where("placeDate").lte(toTime))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.SELL))
                .with(new Sort(Sort.Direction.ASC, "placeDate"))
                .limit(size);

        //.with(new Sort(Sort.Direction.DESC, "itemPrice", "placeDate").and(new Sort(Sort.Direction.ASC, "placeDate"))) 
        final List<Order> sellOrders = mongoTemplate.find(query, Order.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Order Queried with {} : {}", query, sellOrders);
        }
        return sellOrders;
    }

    @Override
    public Order findPendingOrder(String id) {
        hasLength(id);

        if (logger.isDebugEnabled()) {
            logger.debug("Querying pending sell order with id:{}", id);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("primaryKey").is(id))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .limit(1);

        final Order order = mongoTemplate.findOne(query, Order.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Queried with {} : {}", query, order);
        }
        return order;
    }

    @Override
    public Order findLowestPricePendingSellOrder(OrderBookId orderBookId) {
        notNull(orderBookId);
        hasLength(orderBookId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("Lowest sell Price for OrderBook {}", orderBookId);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.SELL))
                .with(new Sort(Sort.Direction.ASC, "itemPrice.amount"))
                .limit(1);

        final Order order = mongoTemplate.findOne(query, Order.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Lowest queried with {} : {}", query, order);
        }
        return order;
    }

    @Override
    public List<Order> findDescPendingBuyOrdersByPriceTime(Date toTime, BigMoney price, OrderBookId orderBookId, int size) {
        notNull(toTime);
        notNull(price);
        notNull(orderBookId);
        hasLength(orderBookId.toString());
        isTrue(size > 0);

        if (logger.isDebugEnabled()) {
            logger.debug("Querying pending buy orders with toDate:{}, price:{}, order book id:{}, size:{}",
                    toTime, price, orderBookId, size);
        }

        CurrencyUnit unit = price.getCurrencyUnit();

        //init stored long valued money 
        long stored = price.rounded(0, RoundingMode.HALF_EVEN).getAmount().longValue();

        if (unit.getDecimalPlaces() >= 0) {
            BigDecimal scale = BigDecimal.TEN.pow(unit.getDecimalPlaces());
            stored = price.multipliedBy(scale).rounded(0, RoundingMode.HALF_EVEN).getAmount().longValue();
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                .addCriteria(Criteria.where("itemPrice.amount").gte(stored))
                .addCriteria(Criteria.where("itemPrice.currency").is(price.getCurrencyUnit().getCurrencyCode()))
                .addCriteria(Criteria.where("placeDate").lte(toTime))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.BUY))
                .with(new Sort(Sort.Direction.DESC, "itemPrice.amount")
                        .and(new Sort(Sort.Direction.ASC, "placeDate")).and(new Sort(Sort.Direction.DESC, "itemRemaining.amount")))
                .limit(size);

        //.with(new Sort(Sort.Direction.DESC, "itemPrice", "placeDate").and(new Sort(Sort.Direction.ASC, "placeDate"))) 
        final List<Order> buyOrders = mongoTemplate.find(query, Order.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Buy order Queried with {} : {}", query, buyOrders);
        }
        return buyOrders;
    }

    @Override
    public Order findHighestPricePendingBuyOrder(OrderBookId orderBookId) {
        notNull(orderBookId);
        hasLength(orderBookId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("Highest buy Price for OrderBook {}", orderBookId);
        }

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookId").is(orderBookId))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .addCriteria(Criteria.where("orderType").is(OrderType.BUY))
                .with(new Sort(Sort.Direction.DESC, "itemPrice.amount"))
                .limit(1);

        final Order order = mongoTemplate.findOne(query, Order.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Highest buy queried with {} : {}", query, order);
        }
        return order;
    }
} 
