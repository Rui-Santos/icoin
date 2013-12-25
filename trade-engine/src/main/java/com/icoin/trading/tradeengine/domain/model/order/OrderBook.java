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

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.events.order.BuyOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.OrderBookCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedHighestBuyPriceEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedLowestSellPriceEvent;
import com.icoin.trading.tradeengine.domain.events.order.SellOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.util.Date;

/**
 * @author Allard Buijze
 */
public class OrderBook extends AbstractAnnotatedAggregateRoot {
    private static final long serialVersionUID = 6778782949492587631L;


    @AggregateIdentifier
    private OrderBookId orderBookId;
    private CurrencyPair currencyPair;

    private BigMoney highestBuyPrice;
    private BigMoney lowestSellPrice;
    private BigMoney tradedPrice;

    @SuppressWarnings("UnusedDeclaration")
    protected OrderBook() {
    }

    public OrderBook(OrderBookId orderBookId, CurrencyPair currencyPair) {
        apply(new OrderBookCreatedEvent(orderBookId, currencyPair));
    }

    public void addBuyOrder(OrderId orderId,
                            TransactionId transactionId,
                            BigMoney tradeCount,
                            BigMoney itemPrice,
                            PortfolioId portfolioId,
                            Date placeDate) {

        apply(new BuyOrderPlacedEvent(
                orderBookId,
                orderId,
                transactionId,
                tradeCount,
                itemPrice,
                portfolioId,
                currencyPair,
                placeDate));
    }

    public void addSellOrder(OrderId orderId, TransactionId transactionId, BigMoney tradeCount,
                             BigMoney itemPrice, PortfolioId portfolioId, Date placeDate) {

        apply(new SellOrderPlacedEvent(
                orderBookId,
                orderId,
                transactionId,
                tradeCount,
                itemPrice,
                portfolioId,
                currencyPair,
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
                BigDecimal matchedTradeAmount = highestBuyer.getItemRemaining().min(lowestSeller.getItemRemaining());

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

    public void resetHighestBuyPrice(String buyOrderId, BigMoney highestBuyPrice) {
        apply(new RefreshedHighestBuyPriceEvent(orderBookId, buyOrderId, highestBuyPrice));
    }

    public void resetLowestSellPrice(String sellOrderId, BigMoney lowestSellPrice) {
        apply(new RefreshedLowestSellPriceEvent(orderBookId, sellOrderId, lowestSellPrice));
    }

    //transaction: to add sell orders / buyer orders
    public void executeSelling(BigMoney matchedTradeAmount,
                               BigMoney matchedTradePrice,
                               String buyOrderId,
                               String sellOrderId,
                               BigMoney buyCommission,
                               BigMoney sellCommission,
                               TransactionId buyTransactionId,
                               TransactionId sellTransactionId,
                               Date tradedDate) {
        apply(new TradeExecutedEvent(orderBookId,
                new CoinId(currencyPair.getBaseCurrency()),
                matchedTradeAmount,
                matchedTradePrice,
                buyOrderId,
                sellOrderId,
                buyCommission,
                sellCommission,
                buyTransactionId,
                sellTransactionId,
                tradedDate,
                TradeType.SELL));
    }

    public void executeBuying(BigMoney matchedTradeAmount,
                              BigMoney matchedTradePrice,
                              String buyOrderId,
                              String sellOrderId,
                              BigMoney buyCommission,
                              BigMoney sellCommission,
                              TransactionId buyTransactionId,
                              TransactionId sellTransactionId,
                              Date tradedDate) {
        apply(new TradeExecutedEvent(
                orderBookId,
                new CoinId(currencyPair.getBaseCurrency()),
                matchedTradeAmount,
                matchedTradePrice,
                buyOrderId,
                sellOrderId,
                buyCommission,
                sellCommission,
                buyTransactionId,
                sellTransactionId,
                tradedDate,
                TradeType.BUY));
    }

    @SuppressWarnings("unused")
    @EventHandler
    protected void onOrderBookCreated(OrderBookCreatedEvent event) {
        this.orderBookId = event.getOrderBookIdentifier();
        this.currencyPair = event.getCurrencyPair();

        highestBuyPrice = BigMoney.zero(CurrencyUnit.of(currencyPair.getCounterCurrency()));
        lowestSellPrice = BigMoney.of(CurrencyUnit.of(currencyPair.getCounterCurrency()), Constants.INIT_SELL_PRICE);
        tradedPrice = BigMoney.zero(CurrencyUnit.of(currencyPair.getCounterCurrency()));
    }

    @SuppressWarnings("unused")
    @EventHandler
    protected void onTradeExecuted(TradeExecutedEvent event) {
        this.tradedPrice = event.getTradedPrice();
    }

    @SuppressWarnings("unused")
    @EventHandler
    protected void onRefreshedLowestSellPrice(RefreshedLowestSellPriceEvent event) {
        this.lowestSellPrice = event.getPrice();
    }

    @SuppressWarnings("unused")
    @EventHandler
    protected void onRefreshedHighestBuyPrice(RefreshedHighestBuyPriceEvent event) {
        this.highestBuyPrice = event.getPrice();
    }

    public BigMoney getHighestBuyPrice() {
        return highestBuyPrice;
    }

    public BigMoney getLowestSellPrice() {
        return lowestSellPrice;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }
}
