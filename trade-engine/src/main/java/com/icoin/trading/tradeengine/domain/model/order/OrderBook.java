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

package com.icoin.trading.tradeengine.domain.model.order;

import com.icoin.trading.tradeengine.domain.events.order.BuyOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.OrderBookCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedBuyOrdersToOrderBookEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedSellOrdersToOrderBookEvent;
import com.icoin.trading.tradeengine.domain.events.order.SellOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinExchangePair;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.homhon.util.Collections.isEmpty;

/**
 * @author Allard Buijze
 */
public class OrderBook extends AbstractAnnotatedAggregateRoot {
    public static final int MIN_THREHOLD = 1000;
    public static final int MAX_THREHOLD = 20000;
    private static Logger logger = LoggerFactory.getLogger(OrderBook.class);
    private static final long serialVersionUID = 6778782949492587631L;


    @AggregateIdentifier
    private OrderBookId orderBookId;

    private CoinExchangePair coinExchangePair;

    private CoinId coinId;

    private BigDecimal highestBuyPrice;
    private BigDecimal lowestSellPrice;
    private BigDecimal executedPrice;

    private transient OrderComparator<SellOrder> sellOrderOrderComparator =
            new OrderComparator<SellOrder>();
    private transient OrderComparator<BuyOrder> buyOrderOrderComparator =
            new OrderComparator<BuyOrder>();

    //todo concurrent support
    private transient NavigableSet<BuyOrder> buyOrders = new ConcurrentSkipListSet<BuyOrder>(new OrderComparator());
    private transient NavigableSet<SellOrder> sellOrders = new TreeSet<SellOrder>(new OrderComparator());

    public SortedSet<BuyOrder> getBuyOrders() {
        return buyOrders;
    }

    public SortedSet<SellOrder> getSellOrders() {
        return sellOrders;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected OrderBook() {
    }

    public OrderBook(OrderBookId primaryKey, CoinId coinId, CoinExchangePair coinExchangePair) {
        apply(new OrderBookCreatedEvent(primaryKey, coinId, coinExchangePair));
    }

    public void addBuyOrder(OrderId orderId,
                            TransactionId transactionId,
                            BigDecimal tradeCount,
                            BigDecimal itemPrice,
                            PortfolioId portfolioId,
                            Date placeDate) {

        apply(new BuyOrderPlacedEvent(
                orderBookId,
                orderId,
                transactionId,
                tradeCount,
                itemPrice,
                portfolioId,
                coinExchangePair,
                placeDate));
        executeTrades();
    }

    public void addSellOrder(OrderId orderId, TransactionId transactionId, BigDecimal tradeCount,
                             BigDecimal itemPrice, PortfolioId portfolioId, Date placeDate) {
        buyOrders.last();
//        while(itemPrice.compareTo()){
//
//        }

        apply(new SellOrderPlacedEvent(
                orderBookId,
                orderId,
                transactionId,
                tradeCount,
                itemPrice,
                portfolioId,
                coinExchangePair,
                placeDate));
        executeTrades();
    }

    private void executeTrades() {
        boolean tradingDone = false;
        while (!tradingDone && !buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            BuyOrder highestBuyer = buyOrders.last();
            SellOrder lowestSeller = sellOrders.first();
            if (highestBuyer.getItemPrice().compareTo(lowestSeller.getItemPrice()) >= 0) {
                //highestBuyer.price >= lowestSeller.price
                BigDecimal matchedTradeAmount = highestBuyer.getItemsRemaining().min(lowestSeller.getItemsRemaining());

                //todo price method
                BigDecimal matchedTradePrice = (highestBuyer.getItemPrice().add(lowestSeller.getItemPrice()).divide(
                        BigDecimal.valueOf(2), 2, RoundingMode.HALF_EVEN));

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing orders with amount {}, price {}: highest buying order {}, lowest selling order {}",
                            matchedTradeAmount, matchedTradePrice, highestBuyer, lowestSeller);
                }

                apply(new TradeExecutedEvent(orderBookId,
                        matchedTradeAmount,
                        matchedTradePrice,
                        highestBuyer.getPrimaryKey(),
                        lowestSeller.getPrimaryKey(),
                        highestBuyer.getTransactionId(),
                        lowestSeller.getTransactionId()));
            } else {
                tradingDone = true;
            }
        }


//        if(buyOrders.size() <= MAX_THREHOLD){
//            apply(new RefreshingBuyOrdersEvent());
//        }
    }

    @EventHandler
    protected void onOrderBookCreated(OrderBookCreatedEvent event) {
        this.orderBookId = event.getOrderBookIdentifier();
        this.coinExchangePair = event.getCoinExchangePair();
        this.coinId = event.getCoinId();
    }


    @EventHandler     //onRefreshingSellOrders
    protected void onRefreshingBuyOrders(RefreshedBuyOrdersToOrderBookEvent event) {
        if (isEmpty(event.getPriorityBuyOrders())) {
            return;
        }

        this.buyOrders.addAll(event.getPriorityBuyOrders());
    }

    @EventHandler
    protected void onRefreshingSellOrders(RefreshedSellOrdersToOrderBookEvent event) {
        if (isEmpty(event.getPrioritySellOrders())) {
            return;
        }

        this.sellOrders.addAll(event.getPrioritySellOrders());
    }

    /*
    public Order(OrderId orderId, TransactionId transactionId, BigDecimal itemPrice,
                 BigDecimal tradeAmount, PortfolioId portfolioId,
                 CoinExchangePair coinExchangePair,
                 Date placeDate) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.itemPrice = itemPrice;
        this.tradeAmount = tradeAmount;
        this.itemsRemaining = tradeAmount;
        this.portfolioId = portfolioId;
        this.coinExchangePair = coinExchangePair;
        this.placeDate = placeDate;
    }*/
    @EventHandler
    protected void onBuyPlaced(BuyOrderPlacedEvent event) {
        final BuyOrder order = new BuyOrder();
        order.setPrimaryKey(event.getOrderId().toString());
        order.setTransactionId(event.getTransactionIdentifier());
        order.setItemPrice(event.getItemPrice());
        order.setTradeAmount(event.getTradeAmount());
        order.setPortfolioId(event.getPortfolioId());
        order.setCoinExchangePair(event.getCoinExchangePair());
        order.setPlaceDate(event.getPlaceDate());



        buyOrders.add(order);
    }

    @EventHandler
    protected void onSellPlaced(SellOrderPlacedEvent event) {
        final SellOrder order = new SellOrder();
        order.setPrimaryKey(event.getOrderId().toString());
        order.setTransactionId(event.getTransactionIdentifier());
        order.setItemPrice(event.getItemPrice());
        order.setTradeAmount(event.getTradeAmount());
        order.setPortfolioId(event.getPortfolioId());
        order.setCoinExchangePair(event.getCoinExchangePair());
        order.setPlaceDate(event.getPlaceDate());



        final SellOrder last = sellOrders.first();
        sellOrderOrderComparator.compare(last,order);


        sellOrders.add(order);
    }

    @EventHandler
    protected void onTradeExecuted(TradeExecutedEvent event) {
        BuyOrder highestBuyer = buyOrders.last();
        SellOrder lowestSeller = sellOrders.first();
        if (highestBuyer.getItemsRemaining().compareTo(event.getTradeAmount()) <= 0) {
            buyOrders.remove(highestBuyer);
        }
        if (lowestSeller.getItemsRemaining().compareTo(event.getTradeAmount()) <= 0) {
            sellOrders.remove(lowestSeller);
        }
    }


    public void resetHighestBuyPrice(BigDecimal highestBuyPrice) {

        //applying to event
        this.highestBuyPrice = highestBuyPrice;
    }

    public void resetExecutedPrice(BigDecimal executedPrice) {
        this.executedPrice = executedPrice;
    }

    public void resetLowestSellPrice(BigDecimal lowestSellPrice) {
        this.lowestSellPrice = lowestSellPrice;
    }

    public BigDecimal getHighestBuyPrice() {
        return highestBuyPrice;
    }

    public BigDecimal getLowestSellPrice() {
        return lowestSellPrice;
    }

    public BigDecimal getExecutedPrice() {
        return executedPrice;
    }
}
