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

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import com.icoin.trading.tradeengine.query.tradeexecuted.OpenHighLowCloseVolume;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Jettro Coenradie
 */
public class TradeExecutedQueryRepositoryImpl implements TradeExecutedQueryRepositoryCustom {

    private static final String TRADE_EXECUTED_ENTRY_COLLECTION = "tradeExecutedEntry";
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

        DBObject command = BasicDBObjectBuilder.start("aggregate", TRADE_EXECUTED_ENTRY_COLLECTION)
                .get();

        final DBObject criteriaObject = Criteria.where("orderBookIdentifier").is(orderBookIdentifier)
                .and("tradeTime").gte(start).lt(end)
                .getCriteriaObject();
        DBObject match = BasicDBObjectBuilder.start("$match", criteriaObject)
                .get();

        DBObject projection = BasicDBObjectBuilder.start("year", BasicDBObjectBuilder.start("$year", "$tradeTime").get())
                .append("month", BasicDBObjectBuilder.start("$month", "$tradeTime").get())
                .append("day", BasicDBObjectBuilder.start("$dayOfMonth", "$tradeTime").get())
                .append("hour", BasicDBObjectBuilder.start("$hour", "$tradeTime").get())
                .append("minute", BasicDBObjectBuilder.start("$minute", "$tradeTime").get())
                .append("tradedPrice", 1)
                .append("tradedAmount", 1)
                .append("_id", 0)
                .get();

        DBObject project = BasicDBObjectBuilder.start("$project", projection)
                .get();

        //{ "aggregate" : "tradeExecutedEntry" ,
        // "pipeline" : [ { "$match" : { "orderBookIdentifier" : "f830f7e3-9f99-4688-92e7-6dbafc7220a8" ,
        // "tradeTime" : { "$gte" : { "$date" : "2007-12-12T04:12:12.120Z"} ,
        // "$lt" : { "$date" : "2012-12-12T04:12:04.120Z"}}}} ,
        // { "$project" : { "tradeTime" : 1 , "tradedPrice.amount" : 1 , "tradedAmount.amount" : 1 , "year" :
        // { "$year" : [ "$tradeTime"]} , "month" : { "$month" : [ "$tradeTime"]} , "week" : { "$week" : [ "$tradeTime"]}}} ,
        // { "$group" : { "_id" : "$year" , "open" : { "$first" : "$tradedPrice"} , "high" : { "$max" : "$tradedPrice"} ,
        // "low" : { "$min" : "$tradedPrice"} , "close" : { "$last" : "$tradedPrice"} , "volume" : { "$sum" : "$tradedAmount"}}} ,
        // { "$skip" : 0} , { "$limit" : 100}]}

//        {"$project": {
//            "year":       {"$year": "$dt"},
//            "month":      {"$month": "$dt"},
//            "day":        {"$dayOfMonth": "$dt"},
//            "hour":       {"$hour": "$dt"},
//            "minute":     {"$minute": "$dt"},
//            "second":     {"$second": "$dt"},
//            "dt": 1,
//                    "p": 1 }},
//        {"_id" : {"year": "$year", "month": "$month", "day": "$day", "hour": "$hour", "minute": "$minute" },
//            "open":  {"$first": "$p"},
//            "high":  {"$max": "$p"},
//            "low":   {"$min": "$p"},
//            "close": {"$last": "$p"} }} ] )

//        02:41:22.649 [main] DEBUG c.i.t.t.q.t.r.TradeExecutedQueryRepositoryImpl - aggregation { "aggregate" : "tradeExecutedEntry" , "pipeline" : [ { "$match" : { "orderBookIdentifier" : "c623022b-9baa-437a-a70f-b59adead3ecf" , "tradeTime" : { "$gte" : { "$date" : "2007-12-12T04:12:12.120Z"} , "$lt" : { "$date" : "2012-12-12T04:12:04.120Z"}}}} , { "$project" : { "year" : { "$year" : "$tradeTime"} , "month" : { "$month" : "$tradeTime"} , "day" : { "$dayOfMonth" : "$tradeTime"} , "hour" : { "$hour" : "$tradeTime"} , "minute" : { "$minute" : "$tradeTime"} , "tradedPrice" : 1 , "tradedAmount" : 1 , "_id" : 0}} , { "$group" : { "_id" : { "year" : "$year" , "priceCcy" : "$tradedPrice.currency" , "amountCcy" : "$tradedAmount.currency"} , "open" : { "$first" : "$tradedPrice.amount"} , "high" : { "$max" : "$tradedPrice.amount"} , "low" : { "$min" : "$tradedPrice.amount"} , "close" : { "$last" : "$tradedPrice.amount"} , "volume" : { "$sum" : "$tradedAmount.amount"}}}]} found :[ { "_id" : { "year" : 2012 , "priceCcy" : "CNY" , "amountCcy" : "BTC"} , "open" : 10500 , "high" : 10500 , "low" : 10500 , "close" : 10500 , "volume" : 11550000000} , { "_id" : { "year" : 2010 , "priceCcy" : "CNY" , "amountCcy" : "BTC"} , "open" : 10500 , "high" : 10500 , "low" : 10500 , "close" : 10500 , "volume" : 2100000000} , { "_id" : { "year" : 2011 , "priceCcy" : "CNY" , "amountCcy" : "BTC"} , "open" : 10500 , "high" : 10500 , "low" : 10500 , "close" : 10500 , "volume" : 1050000000}]
//        02:46:45.023 [main] DEBUG c.i.t.t.q.t.r.TradeExecutedQueryRepositoryImpl - aggregation { "aggregate" : "tradeExecutedEntry" , "pipeline" : [ { "$match" : { "orderBookIdentifier" : "04527652-b53b-47bf-967d-2001fbe18c13" , "tradeTime" : { "$gte" : { "$date" : "2007-12-12T04:12:12.120Z"} , "$lt" : { "$date" : "2012-12-12T04:12:04.120Z"}}}} , { "$project" : { "year" : { "$year" : "$tradeTime"} , "month" : { "$month" : "$tradeTime"} , "day" : { "$dayOfMonth" : "$tradeTime"} , "hour" : { "$hour" : "$tradeTime"} , "minute" : { "$minute" : "$tradeTime"} , "tradedPrice" : 1 , "tradedAmount" : 1 , "_id" : 0}} , { "$group" : { "_id" : { "year" : "$year" , "priceCcy" : "$tradedPrice.currency" , "amountCcy" : "$tradedAmount.currency"} , "open" : { "$first" : "$tradedPrice.amount"} , "high" : { "$max" : "$tradedPrice.amount"} , "low" : { "$min" : "$tradedPrice.amount"} , "close" : { "$last" : "$tradedPrice.amount"} , "volume" : { "$sum" : "$tradedAmount.amount"}}}]} found :[ { "_id" : { "year" : 2012 , "priceCcy" : "CNY" , "amountCcy" : "BTC"} , "open" : 10500 , "high" : 10500 , "low" : 10500 , "close" : 10500 , "volume" : 11550000000} , { "_id" : { "year" : 2010 , "priceCcy" : "CNY" , "amountCcy" : "BTC"} , "open" : 10500 , "high" : 10500 , "low" : 10500 , "close" : 10500 , "volume" : 2100000000} , { "_id" : { "year" : 2011 , "priceCcy" : "CNY" , "amountCcy" : "BTC"} , "open" : 10500 , "high" : 10500 , "low" : 10500 , "close" : 10500 , "volume" : 1050000000}]


        final DBObject groupId = BasicDBObjectBuilder.start("year", "$year")
//                .append("month", "$month")
//                .append("day", "$dayOfMonth")
//                .append("hour", "$hour")
//                .append("minute", "$minute")
                .append("priceCcy", "$tradedPrice.currency")
                .append("amountCcy", "$tradedAmount.currency")
                .get();
        DBObject groupOp = BasicDBObjectBuilder.start("_id", groupId)
                .append("open", BasicDBObjectBuilder.start("$first", "$tradedPrice.amount").get())
                .append("high", BasicDBObjectBuilder.start("$max", "$tradedPrice.amount").get())
                .append("low", BasicDBObjectBuilder.start("$min", "$tradedPrice.amount").get())
                .append("close", BasicDBObjectBuilder.start("$last", "$tradedPrice.amount").get())
                .append("volume", BasicDBObjectBuilder.start("$sum", "$tradedAmount.amount").get())
                .get();

        DBObject group = BasicDBObjectBuilder.start("$group", groupOp)
                .get();


        final BasicDBList pipeline = new BasicDBList();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(group);
        command.put("pipeline", pipeline);


        CommandResult commandResult = mongoTemplate.executeCommand(command);
        handleCommandError(commandResult, command);

        // map results
        @SuppressWarnings("unchecked")
        Iterable<DBObject> resultSet = (Iterable<DBObject>) commandResult.get("result");
        List<OpenHighLowCloseVolume> mappedResults = Lists.newLinkedList();


        if (logger.isDebugEnabled()) {
            logger.debug("aggregation {} found :{}", command, resultSet);
        }

        System.err.println(Long.MAX_VALUE / 100000000);
        return null;
    }

    /**
     * Inspects the given {@link CommandResult} for erros and potentially throws an
     * {@link org.springframework.dao.InvalidDataAccessApiUsageException} for that error.
     *
     * @param result must not be {@literal null}.
     * @param source must not be {@literal null}.
     */
    private void handleCommandError(CommandResult result, DBObject source) {

        try {
            result.throwOnError();
        } catch (MongoException ex) {

            String error = result.getErrorMessage();
            error = error == null ? "NO MESSAGE" : error;

            throw new InvalidDataAccessApiUsageException("Command execution failed:  Error [" + error + "], Command = "
                    + source, ex);
        }
    }

//    //    @Override
//    public List<OpenHighLowCloseVolume> ohlc0(String orderBookIdentifier, Date start, Date end, Pageable pageable) {
//        hasLength(orderBookIdentifier);
//        notNull(pageable);
//
////        db.mycollection.aggregate([
////                {"$match": {"dt": {"$lt" : end_dt, "$gte" : start_dt  }}},
////        {"$project": {
////            "year":       {"$year": "$dt"},
////            "month":      {"$month": "$dt"},
////            "day":        {"$dayOfMonth": "$dt"},
////            "hour":       {"$hour": "$dt"},
////            "minute":     {"$minute": "$dt"},
////            "second":     {"$second": "$dt"},
////            "dt": 1,
////                    "p": 1 }},
////        {"$sort": {"dt": 1}},
////        {"$group":
////            {"_id" : {"year": "$year", "month": "$month", "day": "$day", "hour": "$hour", "minute": "$minute" },
////                "open":  {"$first": "$p"},
////                "high":  {"$max": "$p"},
////                "low":   {"$min": "$p"},
////                "close": {"$last": "$p"} }} ] )
//
//        //order is: match, order, sort, limit
//        final ProjectionOperation project = project(
//                Fields.from(Fields.field("tradeTime"),
//                        Fields.field("tradedPrice.amount", "tradedPrice"),
//                        Fields.field("tradedAmount.amount", "tradeAmount")
////                        Fields.field("priceCurrency", "tradedPrice.currency"),
////                Fields.field("tradeCurrency", "tradedAmount.currency"))
//                ));
//
//        //{ "aggregate" : "tradeExecutedEntry" ,
//        // "pipeline" : [ { "$match" : { "orderBookIdentifier" : "f830f7e3-9f99-4688-92e7-6dbafc7220a8" ,
//        // "tradeTime" : { "$gte" : { "$date" : "2007-12-12T04:12:12.120Z"} ,
//        // "$lt" : { "$date" : "2012-12-12T04:12:04.120Z"}}}} ,
//        // { "$project" : { "tradeTime" : 1 , "tradedPrice.amount" : 1 , "tradedAmount.amount" : 1 , "year" :
//        // { "$year" : [ "$tradeTime"]} , "month" : { "$month" : [ "$tradeTime"]} , "week" : { "$week" : [ "$tradeTime"]}}} ,
//        // { "$group" : { "_id" : "$year" , "open" : { "$first" : "$tradedPrice"} , "high" : { "$max" : "$tradedPrice"} ,
//        // "low" : { "$min" : "$tradedPrice"} , "close" : { "$last" : "$tradedPrice"} , "volume" : { "$sum" : "$tradedAmount"}}} ,
//        // { "$skip" : 0} , { "$limit" : 100}]}
//
//        //        { "aggregate" : "tradeExecutedEntry" ,
//        // "pipeline" : [ { "$match" : { "orderBookIdentifier" : "4ed97c9a-d391-477a-bbdc-cbe4c41865d9" ,
//        // "tradeTime" : { "$gte" : { "$date" : "2007-12-12T04:12:12.120Z"} ,
//        // "$lt" : { "$date" : "2012-12-12T04:12:04.120Z"}}}} ,
//        // { "$project" : { "tradeTime" : 1 , "tradedPrice.amount" : "$tradedPrice" , "tradedAmount.amount" : "$tradeAmount" , "year" : { "$year" : [ "$tradeTime"]} , "month" : { "$month" : [ "$tradeTime"]} , "week" : { "$week" : [ "$tradeTime"]}}} , { "$group" : { "_id" : "$year" , "open" : { "$first" : "$tradedPrice"} , "high" : { "$max" : "$tradedPrice"} , "low" : { "$min" : "$tradedPrice"} , "close" : { "$last" : "$tradedPrice"} , "volume" : { "$sum" : "$tradeAmount"}}} , { "$skip" : 0} , { "$limit" : 100}]}
//
//        System.out.println(project.getFields());
//        Aggregation aggregation = newAggregation(TradeExecutedEntry.class,
//                match(where("orderBookIdentifier").is(orderBookIdentifier)
//                        .and("tradeTime").gte(start).lt(end)),
////                project("tradeTime", "tradedPrice.amount", "tradedPrice.currency","tradedAmount.amount","tradedAmount.currency")
//                project
////                        .and("tradedAmount.amount").as("tradedAmount")
////                        .and("tradedPrice.amount").as("tradedPrice")
////                        .and("tradedAmount").nested(bind("amount", "tradedAmount"))
////                        .and("tradedAmount.amount").as("tradedA")
//                        .andExpression("year(tradeTime)").as("year") //
//                        .andExpression("month(tradeTime)").as("month") //
//                        .andExpression("week(tradeTime)").as("week") //
////                        .and("tradeTime").project("hour").as("hour")
////                .and("tradeTime").project("minute").as("minute")
////                .and("tradeTime").project("second").as("second")
//                ,
////                group(Fields.from(Fields.field("year", "year"))),
////                sort(DESC, "tradeTime", "tradedAmount.amount"),
////                project()
////                .and("year").as("year")
////                        ,
//                group("year")
//                        .first("tradedPrice").as("open")
//                        .max("tradedPrice").as("high")
//                        .min("tradedPrice").as("low")
//                        .last("tradedPrice").as("close")
//                        .sum("tradeAmount").as("volume"),
////                group(Fields.fields("year"))
////                        /*.and(Fields.field("amountCurrency")*///)
//////                        .and(Fields.field("year", "year"))
//////                        .and(Fields.field("month", "month"))
//////                        .and(Fields.field("day", "day"))
//////                        .and(Fields.field("hour", "hour"))
//////                )
////                        .first("tradedPrice").as("open")
////                        .max("tradedPrice").as("high")
////                        .min("tradedPrice").as("low")
////                        .last("tradedPrice").as("close")
////                        .sum("tradedAmount.amount").as("volume"),
//                skip(pageable.getOffset()),
//                limit(pageable.getPageSize())
//        );
//
//
//        AggregationResults<DBObject> result = mongoTemplate.aggregate(aggregation, "tradeExecutedEntry", DBObject.class);
//
//        List<DBObject> openHighLowCloseVolumes = result.getMappedResults();
//
//        if (logger.isDebugEnabled()) {
//            logger.debug("aggregation {} found :{}", aggregation, openHighLowCloseVolumes);
//        }
//        return null;
//    }

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
