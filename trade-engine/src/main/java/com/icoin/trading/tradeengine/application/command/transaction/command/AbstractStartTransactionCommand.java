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

package com.icoin.trading.tradeengine.application.command.transaction.command;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public abstract class AbstractStartTransactionCommand<T extends AbstractStartTransactionCommand> extends CommandSupport<T> {

    private TransactionId transactionId;
    private CoinId coinId;
    private CurrencyPair currencyPair;
    private OrderBookId orderBookIdentifier;
    private PortfolioId portfolioIdentifier;
    private BigMoney tradeAmount;
    private BigMoney itemPrice;
    private Date time;

    public AbstractStartTransactionCommand(TransactionId transactionId,
                                           CoinId coinId,
                                           CurrencyPair currencyPair,
                                           OrderBookId orderBookIdentifier,
                                           PortfolioId portfolioIdentifier,
                                           BigMoney tradeAmount,
                                           BigMoney itemPrice,
                                           Date time) {
        this.transactionId = transactionId;
        this.coinId = coinId;
        this.currencyPair = currencyPair;
        this.orderBookIdentifier = orderBookIdentifier;
        this.portfolioIdentifier = portfolioIdentifier;
        this.tradeAmount = tradeAmount;
        this.itemPrice = itemPrice;
        this.time = time;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public BigMoney getItemPrice() {
        return itemPrice;
    }

    public OrderBookId getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionId;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public Date getTime() {
        return time;
    }
}
