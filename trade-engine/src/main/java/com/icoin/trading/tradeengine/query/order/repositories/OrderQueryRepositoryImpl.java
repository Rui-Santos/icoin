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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import java.util.Date;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.previousOperation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Jettro Coenradie
 */
public class OrderQueryRepositoryImpl implements OrderQueryRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(OrderQueryRepositoryImpl.class);

    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<PriceAggregate> findOrderAggregatedPrice(String orderBookIdentifier, OrderType type, Date toDate) {

        //order is: match, order, sort, limit
        TypedAggregation<OrderEntry> aggregation = newAggregation(OrderEntry.class,
                match(where("orderStatus").is(OrderStatus.PENDING.toString())
                        .and("type").is(type.toString())
                        .and("itemRemaining.amount").gt(0)
                        .and("placedDate").lte(toDate)
                        .and("orderBookIdentifier").is(orderBookIdentifier)),
//                project("itemPrice.amount","itemRemaining.amount")
//                .and("itemPrice.amount").as("amount1")
//                .and("itemRemaining.amount").as("amount2"),
                group("itemPrice.amount", "itemPrice.currency", "itemRemaining.currency").sum("itemRemaining.amount").as("sum"),
                project("sum")
                        .and("itemPrice.currency").as("ccy")
                        .and("itemPrice.amount").as("amount"),
                sort(DESC, previousOperation()),
                limit(10)
        );
        AggregationResults<PriceAggregate> result = mongoTemplate.aggregate(aggregation, PriceAggregate.class);

        List<PriceAggregate> priceAggregateList = result.getMappedResults();

        if (logger.isDebugEnabled()) {
            logger.debug("aggregation {} found :{}", aggregation, priceAggregateList);
        }

        return priceAggregateList;
    }
}
