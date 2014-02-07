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

package com.icoin.trading.tradeengine.query.tradeexecuted.repositories;

import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import com.icoin.trading.tradeengine.query.tradeexecuted.OpenHighLowCloseVolume;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.previousOperation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Jettro Coenradie
 */
public class TradeExecutedQueryRepositoryImpl implements TradeExecutedQueryRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(TradeExecutedQueryRepositoryImpl.class);

    private MongoTemplate mongoTemplate;

    @Resource(name = "trade.mongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<OpenHighLowCloseVolume> ohlc(String orderBookIdentifier, Date start, Date end, Pageable pageable) {
        hasLength(orderBookIdentifier);
        notNull(pageable);

//        db.mycollection.aggregate([
//                {"$match": {"dt": {"$lt" : end_dt, "$gte" : start_dt  }}},
//        {"$project": {
//            "year":       {"$year": "$dt"},
//            "month":      {"$month": "$dt"},
//            "day":        {"$dayOfMonth": "$dt"},
//            "hour":       {"$hour": "$dt"},
//            "minute":     {"$minute": "$dt"},
//            "second":     {"$second": "$dt"},
//            "dt": 1,
//                    "p": 1 }},
//        {"$sort": {"dt": 1}},
//        {"$group":
//            {"_id" : {"year": "$year", "month": "$month", "day": "$day", "hour": "$hour", "minute": "$minute" },
//                "open":  {"$first": "$p"},
//                "high":  {"$max": "$p"},
//                "low":   {"$min": "$p"},
//                "close": {"$last": "$p"} }} ] )

        //order is: match, order, sort, limit
        TypedAggregation<TradeExecutedEntry> aggregation = newAggregation(TradeExecutedEntry.class,
                match(where("orderBookIdentifier").is(orderBookIdentifier)
                        .and("tradeTime").gte(start)
                        .and("tradeTime").lt(end)),
                project("tradeTime", "tradedPrice", "tradedAmount")
                        .and("tradeTime").project("year").as("year")
                        .and("tradeTime").project("month").as("month")
                        .and("tradeTime").project("dayOfMonth").as("day")
                        .and("tradeTime").project("hour").as("hour")
//                .and("tradeTime").project("minute").as("year")
//                .and("tradeTime").project("second").as("year")
                ,
                sort(DESC, "tradeTime", "tradedPrice.amount"),
                group(Fields.from(Fields.field("priceCurrency", "tradedPrice.currency"))
                        .and(Fields.field("amountCurrency", "tradedAmount.currency"))
                        .and(Fields.field("year", "year"))
                        .and(Fields.field("month", "month"))
                        .and(Fields.field("day", "day"))
                        .and(Fields.field("hour", "hour"))
                )
                        .first("tradedPrice").as("open")
                        .max("tradedPrice").as("high")
                        .min("tradedPrice").as("low")
                        .last("tradedPrice").as("close")
                        .sum("tradedAmount").as("volume"),
                skip(pageable.getOffset()),
                limit(pageable.getPageSize())
        );


        AggregationResults<OpenHighLowCloseVolume> result = mongoTemplate.aggregate(aggregation, OpenHighLowCloseVolume.class);

        List<OpenHighLowCloseVolume> openHighLowCloseVolumes = result.getMappedResults();

        if (logger.isDebugEnabled()) {
            logger.debug("aggregation {} found :{}", aggregation, openHighLowCloseVolumes);
        }
        return openHighLowCloseVolumes;
    }

//    /**
//     * Applies the given {@link Pageable} to the given {@link MongodbQuery}.
//     *
//     * @param query
//     * @param pageable
//     * @return
//     */
//    private MongodbQuery<T> applyPagination(MongodbQuery<T> query, Pageable pageable) {
//
//        if (pageable == null) {
//            return query;
//        }
//
//        query = query.offset(pageable.getOffset()).limit(pageable.getPageSize());
//        return applySorting(query, pageable.getSort());
//    }
//
//    /**
//     * Applies the given {@link org.springframework.data.domain.Sort} to the given {@link MongodbQuery}.
//     *
//     * @param query
//     * @param sort
//     * @return
//     */
//    private MongodbQuery<T> applySorting(MongodbQuery<T> query, Sort sort) {
//
//        if (sort == null) {
//            return query;
//        }
//
//        for (Sort.Order order : sort) {
//            query.orderBy(toOrder(order));
//        }
//
//        return query;
//    }

    public List<PriceAggregate> findOrderAggregatedPrice(String orderBookIdentifier, OrderType type, Date toDate, int limit) {
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
                sort(DESC, previousOperation()),
                limit(limit)
        );

        AggregationResults<PriceAggregate> result = mongoTemplate.aggregate(aggregation, PriceAggregate.class);

        List<PriceAggregate> priceAggregateList = result.getMappedResults();

        if (logger.isDebugEnabled()) {
            logger.debug("aggregation {} found :{}", aggregation, priceAggregateList);
        }
        return priceAggregateList;
    }
}
