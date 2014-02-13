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

package com.icoin.trading.tradeengine.domain.events.transaction;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public abstract class AbstractTransactionStartedEvent<T extends AbstractTransactionStartedEvent> extends EventSupport<T> {
    private TransactionId transactionIdentifier;
    private CoinId coinId;
    private OrderBookId orderBookIdentifier;
    private PortfolioId portfolioIdentifier;
    private BigMoney totalItem;
    private BigMoney pricePerItem;
    private BigMoney totalMoney;
    private BigMoney totalCommission;

    public AbstractTransactionStartedEvent(TransactionId transactionIdentifier,
                                           CoinId coinId,
                                           OrderBookId orderBookIdentifier,
                                           PortfolioId portfolioIdentifier,
                                           BigMoney totalItem,
                                           BigMoney pricePerItem,
                                           BigMoney totalMoney,
                                           BigMoney totalCommission) {
        this.transactionIdentifier = transactionIdentifier;
        this.coinId = coinId;
        this.orderBookIdentifier = orderBookIdentifier;
        this.portfolioIdentifier = portfolioIdentifier;
        this.totalItem = totalItem;
        this.pricePerItem = pricePerItem;
        this.totalMoney = totalMoney;
        this.totalCommission = totalCommission;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public OrderBookId getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public BigMoney getTotalMoney() {
        return totalMoney;
    }

    public BigMoney getTotalCommission() {
        return totalCommission;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public BigMoney getPricePerItem() {
        return pricePerItem;
    }

    public BigMoney getTotalItem() {
        return totalItem;
    }
}
