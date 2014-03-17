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

package com.icoin.trading.api.tradeengine.events.order;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.coin.events.CurrencyPair;
import com.icoin.trading.api.tradeengine.events.order.OrderBookId;
import com.icoin.trading.api.tradeengine.events.order.OrderId;
import com.icoin.trading.api.tradeengine.events.portfolio.PortfolioId;
import com.icoin.trading.api.tradeengine.events.transaction.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * <p>Abstract parent class for all buy and sell order placed events.</p>
 *
 * @author Allard Buijze
 */
public abstract class AbstractOrderPlacedEvent<T extends AbstractOrderPlacedEvent> extends EventSupport<T> {

    private final OrderBookId orderBookId;
    private final OrderId orderId;
    private TransactionId transactionId;
    private final BigMoney tradeAmount;
    private final BigMoney itemPrice;
    private final BigMoney totalCommission;
    private final PortfolioId portfolioId;
    private final CurrencyPair currencyPair;
    private final Date placeDate;

    protected AbstractOrderPlacedEvent(OrderBookId orderBookId,
                                       OrderId orderId,
                                       TransactionId transactionId,
                                       BigMoney tradeAmount,
                                       BigMoney itemPrice,
                                       BigMoney totalCommission,
                                       PortfolioId portfolioId,
                                       CurrencyPair currencyPair,
                                       Date placeDate) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.tradeAmount = tradeAmount;
        this.itemPrice = itemPrice;
        this.totalCommission = totalCommission;
        this.portfolioId = portfolioId;
        this.orderBookId = orderBookId;
        this.currencyPair = currencyPair;
        this.placeDate = placeDate;
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

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public BigMoney getItemPrice() {
        return itemPrice;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public Date getPlaceDate() {
        return placeDate;
    }

    public BigMoney getTotalCommission() {
        return totalCommission;
    }
}
