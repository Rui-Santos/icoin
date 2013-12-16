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
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * <p>Abstract parent class for all buy and sell order placed events.</p>
 *
 * @author Allard Buijze
 */
public abstract class AbstractOrderPlacedEvent {

    private final OrderBookId orderBookId;
    private final OrderId orderId;
    private TransactionId transactionId;
    private final BigMoney tradeAmount;
    private final BigMoney itemPrice;
    private final PortfolioId portfolioId;
    private final CurrencyPair currencyPair;
    private final Date placeDate;

    protected AbstractOrderPlacedEvent(OrderBookId orderBookId,
                                       OrderId orderId,
                                       TransactionId transactionId,
                                       BigMoney tradeAmount,
                                       BigMoney itemPrice,
                                       PortfolioId portfolioId,
                                       CurrencyPair currencyPair,
                                       Date placeDate) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.tradeAmount = tradeAmount;
        this.itemPrice = itemPrice;
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
}
