/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.tradeengine.query.order.repositories;

import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.hasText;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.previousOperation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Jettro Coenradie
 */
public class OrderQueryRepositoryImpl implements OrderQueryRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(OrderQueryRepositoryImpl.class);

    private MongoTemplate mongoTemplate;

    @Resource(name = "trade.mongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<PriceAggregate> findOrderAggregatedPrice(String orderBookIdentifier, OrderType type, Date toDate, int limit) {
        hasLength(orderBookIdentifier);
        notNull(type);
        notNull(toDate);
        isTrue(limit >= 1);
        //order is: match, order, sort, limit
        TypedAggregation<OrderEntry> aggregation = newAggregation(OrderEntry.class,
                match(where("orderStatus").is(OrderStatus.PENDING.toString())
                        .and("type").is(type.toString())
                        .and("itemRemaining.amount").gt(0)
                        .and("placedDate").lte(toDate)
                        .and("orderBookIdentifier").is(orderBookIdentifier)),
                group(Fields.from(Fields.field("price", "itemPrice.amount"))
                        .and(Fields.field("priceCurrency", "itemPrice.currency"))
                        .and(Fields.field("amountCurrency", "itemRemaining.currency")))
                        .sum("itemRemaining.amount").as("sumUpAmountPerPrice"),
                type == OrderType.BUY ? sort(DESC, previousOperation(), "itemPrice.amount") : sort(ASC, previousOperation(), "itemPrice.amount"),
//                sort(DESC, "itemPrice.amount"),
                limit(limit)
        );

        AggregationResults<PriceAggregate> result = mongoTemplate.aggregate(aggregation, PriceAggregate.class);

        List<PriceAggregate> priceAggregateList = result.getMappedResults();

        if (logger.isDebugEnabled()) {
            logger.debug("findOrderAggregatedPrice {} found :{}", aggregation, priceAggregateList);
        }
        return priceAggregateList;
    }

    @Override
    public List<OrderEntry> findAllUserOrders(String userId, int start, int limit) {
        notNull(userId);
        isTrue(start >= 0);
        isTrue(limit > 0);

        final Query query = new Query()
                .addCriteria(Criteria.where("userId").is(userId))
                .with(new Sort(Sort.Direction.DESC, "placedDate"))
                .skip(start)
                .limit(limit);

        final List<OrderEntry> orders = mongoTemplate.find(query, OrderEntry.class);

        if (logger.isDebugEnabled()) {
            logger.debug("findAllUserOrders Queried with {} : {}", query, orders);
        }
        return orders;
    }

    public List<OrderEntry> findActiveHintSellOrders(String orderBookId, int start, int limit) {
        notNull(orderBookId);
        isTrue(start >= 0);
        isTrue(limit > 0);

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookIdentifier").is(orderBookId))
                .addCriteria(Criteria.where("type").is(OrderType.SELL))
                .with(new Sort(Sort.Direction.ASC, "itemPrice.amount"))
                .skip(start)
                .limit(limit);

        final List<OrderEntry> orders = mongoTemplate.find(query, OrderEntry.class);

        if (logger.isDebugEnabled()) {
            logger.debug("findActiveHintSellOrders Queried with {} : {}", query, orders);
        }
        return orders;
    }

    public List<OrderEntry> findActiveHintBuyOrders(String orderBookId, int start, int limit) {
        notNull(orderBookId);
        isTrue(start >= 0);
        isTrue(limit > 0);

        final Query query = new Query()
                .addCriteria(Criteria.where("orderBookIdentifier").is(orderBookId))
                .addCriteria(Criteria.where("type").is(OrderType.BUY))
                .with(new Sort(Sort.Direction.DESC, "itemPrice.amount"))
                .skip(start)
                .limit(limit);

        final List<OrderEntry> orders = mongoTemplate.find(query, OrderEntry.class);

        if (logger.isDebugEnabled()) {
            logger.debug("findActiveHintBuyOrders Queried with {} : {}", query, orders);
        }
        return orders;
    }

    @Override
    public List<OrderEntry> findUserActiveOrders(String userId, String orderBookId) {
        hasText(userId);
        hasText(orderBookId);

        final Query query = new Query()
                .addCriteria(Criteria.where("userId").is(userId))
                .addCriteria(Criteria.where("orderBookIdentifier").is(orderBookId))
                .addCriteria(Criteria.where("orderStatus").is(OrderStatus.PENDING))
                .with(new Sort(Sort.Direction.DESC, "placedDate"));

        final List<OrderEntry> orders = mongoTemplate.find(query, OrderEntry.class);

        if (logger.isDebugEnabled()) {
            logger.debug("findActiveHintBuyOrders Queried with {} : {}", query, orders);
        }
        return orders;
    }
}
