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

package com.icoin.trading.tradeengine.domain.events.order;

import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;

/**
 * <p>Abstract parent class for all buy and sell order placed events.</p>
 *
 * @author Allard Buijze
 */
public abstract class AbstractOrderPlacedEvent {

    private final OrderBookId orderBookId;
    private final OrderId orderId;
    private TransactionId transactionId;
    private final long tradeCount;
    private final long itemPrice;
    private final PortfolioId portfolioId;

    protected AbstractOrderPlacedEvent(OrderBookId orderBookId, OrderId orderId, TransactionId transactionId,
                                       long tradeCount, long itemPrice, PortfolioId portfolioId) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.tradeCount = tradeCount;
        this.itemPrice = itemPrice;
        this.portfolioId = portfolioId;
        this.orderBookId = orderBookId;
    }

    public OrderBookId orderBookIdentifier() {
        return this.orderBookId;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public long getTradeCount() {
        return tradeCount;
    }

    public long getItemPrice() {
        return itemPrice;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    /**
     * <p>A new OrderBook is created for the coin with the provided identifier.</p>
     *
     * @author Jettro Coenradie
     */
    public static class OrderBookCreatedEvent {

        private OrderBookId orderBookId;
        private CurrencyPair currencyPair;

        public OrderBookCreatedEvent(OrderBookId orderBookId, CurrencyPair currencyPair) {
            this.orderBookId = orderBookId;
            this.currencyPair = currencyPair;
        }

        public OrderBookId getOrderBookIdentifier() {
            return this.orderBookId;
        }
    }
}
