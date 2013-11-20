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

package com.icoin.trading.query.tradeengine.command;

import com.icoin.trading.api.orders.trades.BuyOrderPlacedEvent;
import com.icoin.trading.api.orders.trades.OrderBookCreatedEvent;
import com.icoin.trading.api.orders.trades.OrderId;
import com.icoin.trading.api.orders.trades.SellOrderPlacedEvent;
import com.icoin.trading.api.orders.trades.TradeExecutedEvent;
import com.icoin.trading.api.orders.trades.TransactionId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcedMember;
import com.icoin.trading.api.orders.trades.OrderBookId;
import com.icoin.trading.api.orders.trades.PortfolioId;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Allard Buijze
 */
class OrderBook extends AbstractAnnotatedAggregateRoot {
    private static final long serialVersionUID = 6778782949492587631L;

    @AggregateIdentifier
    private OrderBookId orderBookId;

    @EventSourcedMember
    private SortedSet<Order> buyOrders = new TreeSet<Order>(new OrderComparator());
    @EventSourcedMember
    private SortedSet<Order> sellOrders = new TreeSet<Order>(new OrderComparator());

    @SuppressWarnings("UnusedDeclaration")
    protected OrderBook() {
    }

    public OrderBook(OrderBookId identifier) {
        apply(new OrderBookCreatedEvent(identifier));
    }

    public void addBuyOrder(OrderId orderId, TransactionId transactionId, long tradeCount,
                            long itemPrice, PortfolioId portfolioId) {
        apply(new BuyOrderPlacedEvent(orderBookId, orderId, transactionId, tradeCount, itemPrice, portfolioId));
        executeTrades();
    }

    public void addSellOrder(OrderId orderId, TransactionId transactionId, long tradeCount,
                             long itemPrice, PortfolioId portfolioId) {
        apply(new SellOrderPlacedEvent(orderBookId, orderId, transactionId, tradeCount, itemPrice, portfolioId));
        executeTrades();
    }

    private void executeTrades() {
        boolean tradingDone = false;
        while (!tradingDone && !buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order highestBuyer = buyOrders.last();
            Order lowestSeller = sellOrders.first();
            if (highestBuyer.getItemPrice() >= lowestSeller.getItemPrice()) {
                long matchedTradeCount = Math.min(highestBuyer.getItemsRemaining(), lowestSeller.getItemsRemaining());
                long matchedTradePrice = ((highestBuyer.getItemPrice() + lowestSeller.getItemPrice()) / 2);
                apply(new TradeExecutedEvent(orderBookId,
                        matchedTradeCount,
                        matchedTradePrice,
                        highestBuyer.getOrderId(),
                        lowestSeller.getOrderId(),
                        highestBuyer.getTransactionId(),
                        lowestSeller.getTransactionId()));
            } else {
                tradingDone = true;
            }
        }
    }

    @EventHandler
    protected void onOrderBookCreated(OrderBookCreatedEvent event) {
        this.orderBookId = event.getOrderBookIdentifier();
    }

    @EventHandler
    protected void onBuyPlaced(BuyOrderPlacedEvent event) {
        buyOrders.add(new Order(event.getOrderId(),
                event.getTransactionIdentifier(),
                event.getItemPrice(),
                event.getTradeCount(),
                event.getPortfolioId()));
    }

    @EventHandler
    protected void onSellPlaced(SellOrderPlacedEvent event) {
        sellOrders.add(new Order(event.getOrderId(),
                event.getTransactionIdentifier(),
                event.getItemPrice(),
                event.getTradeCount(),
                event.getPortfolioId()));
    }

    @EventHandler
    protected void onTradeExecuted(TradeExecutedEvent event) {
        Order highestBuyer = buyOrders.last();
        Order lowestSeller = sellOrders.first();
        if (highestBuyer.getItemsRemaining() <= event.getTradeCount()) {
            buyOrders.remove(highestBuyer);
        }
        if (lowestSeller.getItemsRemaining() <= event.getTradeCount()) {
            sellOrders.remove(lowestSeller);
        }
    }

    private static class OrderComparator implements Comparator<Order> {

        public int compare(Order o1, Order o2) {
            // copied from Java 7 Long.compareTo to support java 6
            long x = o1.getItemPrice();
            long y = o2.getItemPrice();
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }
}
