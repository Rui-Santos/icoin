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
import com.icoin.trading.tradeengine.domain.events.order.RefreshedHighestBuyPriceEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedLowestSellPriceEvent;
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
import java.util.Date;

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

    private BuyOrder highestBuy;
    private SellOrder lowestSell;
    private BigDecimal tradedPrice;

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
    }

    public void addSellOrder(OrderId orderId, TransactionId transactionId, BigDecimal tradeCount,
                             BigDecimal itemPrice, PortfolioId portfolioId, Date placeDate) {

        apply(new SellOrderPlacedEvent(
                orderBookId,
                orderId,
                transactionId,
                tradeCount,
                itemPrice,
                portfolioId,
                coinExchangePair,
                placeDate));
    }

    /*
    old logic for hte trading
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
    }*/

    @EventHandler
    protected void onOrderBookCreated(OrderBookCreatedEvent event) {
        this.orderBookId = event.getOrderBookIdentifier();
        this.coinExchangePair = event.getCoinExchangePair();
        this.coinId = event.getCoinId();
    }

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
    }

    @EventHandler
    protected void onTradeExecuted(TradeExecutedEvent event) {
        this.tradedPrice = event.getTradedPrice();
    }


    public void resetHighestBuyPrice(BuyOrder highestBuy) {

        //applying to event
        this.highestBuy = highestBuy;
        apply(new RefreshedHighestBuyPriceEvent());
    }

    public void resetLowestSellPrice(SellOrder lowestSell) {
        this.lowestSell = lowestSell;
        apply(new RefreshedLowestSellPriceEvent());
    }

    public void resetExecutedPrice(BigDecimal executedPrice,
                                   TransactionId buyTransactionId,
                                   TransactionId sellTransactionId) {

        //does it need the transaction id?
        this.tradedPrice = executedPrice;
        apply(new RefreshedCurrentTradedPriceEvent());
    }

    public BigDecimal getHighestBuyPrice() {
        return highestBuy.getItemPrice();
    }

    public BigDecimal getLowestSellPrice() {
        return lowestSell.getItemPrice();
    }

    public BigDecimal getTradedPrice() {
        return tradedPrice;
    }

    //transaction: to add sell orders / buyer orders
    public void executeSelling(BigDecimal matchedTradeAmount,
                               BigDecimal matchedTradePrice,
                               String buyOrderId,
                               String sellOrderId,
                               TransactionId buyTransactionId,
                               TransactionId sellTransactionId) {
        apply(new TradeExecutedEvent(orderBookId,
                matchedTradeAmount,
                matchedTradePrice,
                buyOrderId,
                sellOrderId,
                buyTransactionId,
                sellTransactionId));
    }

    public void executeBuying(BigDecimal matchedTradeAmount,
                              BigDecimal matchedTradePrice,
                              String buyOrderId,
                              String sellOrderId,
                              TransactionId buyTransactionId,
                              TransactionId sellTransactionId) {
        apply(new TradeExecutedEvent(orderBookId,
                matchedTradeAmount,
                matchedTradePrice,
                buyOrderId,
                sellOrderId,
                buyTransactionId,
                sellTransactionId));
    }
}
