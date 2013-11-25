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

import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Allard Buijze
 */
class Order extends AbstractAnnotatedEntity {

    private OrderId orderId;
    private TransactionId transactionId;
    private final BigDecimal itemPrice;
    private final BigDecimal tradeAmount;
    private final PortfolioId portfolioId;
    private BigDecimal itemsRemaining;
    private Date date;
    private CurrencyPair currencyPair;

    public Order(OrderId orderId, TransactionId transactionId, BigDecimal itemPrice,
                 BigDecimal tradeAmount, PortfolioId portfolioId,
                 CurrencyPair currencyPair) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.itemPrice = itemPrice;
        this.tradeAmount = tradeAmount;
        this.itemsRemaining = tradeAmount;
        this.portfolioId = portfolioId;
        this.currencyPair = currencyPair;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public BigDecimal getItemsRemaining() {
        return itemsRemaining;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    private void recordTraded(BigDecimal tradeAmount) {
        this.itemsRemaining.subtract(tradeAmount);
    }

    public TransactionId getTransactionId() {
        return transactionId;
    }

    @EventHandler
    protected void onTradeExecuted(TradeExecutedEvent event) {
        if (orderId.equals(event.getBuyOrderId())
                || orderId.equals(event.getSellOrderId())) {
            recordTraded(event.getTradeAmount());
        }
    }
}
