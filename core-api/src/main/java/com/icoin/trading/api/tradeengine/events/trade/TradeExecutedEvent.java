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

package com.icoin.trading.api.tradeengine.events.trade;


import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.users.domain.UserId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * <p>A new trade has been executed. The event contains the amount of items that are traded and the price for the items
 * that are traded. The event also contains the identifiers for the Buy Order and the Sell order.</p>
 *
 * @author Allard Buijze
 */
public class TradeExecutedEvent extends EventSupport<TradeExecutedEvent> {
    private static final long serialVersionUID = 6292249351659536792L;

    private final BigMoney tradeAmount;
    private final BigMoney tradedPrice;
    private final String buyOrderId;
    private final CoinId coinId;
    private final String sellOrderId;
    private final TransactionId buyTransactionId;
    private final TransactionId sellTransactionId;
    private final OrderBookId orderBookId;
    private final Date tradeTime;
    private final TradeType tradeType;
    private final BigMoney buyCommission;
    private final BigMoney sellCommission;
    private final BigMoney executedMoney;
    private final PortfolioId buyPortfolioId;
    private final PortfolioId sellPortfolioId;
    private final UserId buyUserId;
    private final UserId sellUserId;

    public TradeExecutedEvent(OrderBookId orderBookId,
                              CoinId coinId,
                              BigMoney tradeAmount,
                              BigMoney tradedPrice,
                              BigMoney executedMoney,
                              String buyOrderId,
                              String sellOrderId,
                              BigMoney buyCommission,
                              BigMoney sellCommission,
                              TransactionId buyTransactionId,
                              TransactionId sellTransactionId,
                              PortfolioId buyPortfolioId,
                              PortfolioId sellPortfolioId,
                              UserId buyUserId,
                              UserId sellUserId,
                              Date tradeTime,
                              TradeType tradeType) {
        this.orderBookId = orderBookId;
        this.coinId = coinId;
        this.tradeAmount = tradeAmount;
        this.tradedPrice = tradedPrice;
        this.executedMoney = executedMoney;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyCommission = buyCommission;
        this.sellCommission = sellCommission;
        this.sellTransactionId = sellTransactionId;
        this.buyTransactionId = buyTransactionId;
        this.buyPortfolioId = buyPortfolioId;
        this.sellPortfolioId = sellPortfolioId;
        this.buyUserId = buyUserId;
        this.sellUserId = sellUserId;
        this.tradeTime = tradeTime;
        this.tradeType = tradeType;
    }

    public BigMoney getBuyCommission() {
        return buyCommission;
    }

    public BigMoney getSellCommission() {
        return sellCommission;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public OrderBookId getOrderBookIdentifier() {
        return this.orderBookId;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public TransactionId getBuyTransactionId() {
        return buyTransactionId;
    }

    public TransactionId getSellTransactionId() {
        return sellTransactionId;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public PortfolioId getBuyPortfolioId() {
        return buyPortfolioId;
    }

    public PortfolioId getSellPortfolioId() {
        return sellPortfolioId;
    }

    public UserId getSellUserId() {
        return sellUserId;
    }

    public UserId getBuyUserId() {
        return buyUserId;
    }

    @Override
    public String toString() {
        return "TradeExecutedEvent{" +
                "tradeAmount=" + tradeAmount +
                ", tradedPrice=" + tradedPrice +
                ", buyOrderId='" + buyOrderId + '\'' +
                ", coinId=" + coinId +
                ", sellOrderId='" + sellOrderId + '\'' +
                ", buyTransactionId=" + buyTransactionId +
                ", sellTransactionId=" + sellTransactionId +
                ", orderBookId=" + orderBookId +
                ", tradeTime=" + tradeTime +
                ", tradeType=" + tradeType +
                ", buyCommission=" + buyCommission +
                ", sellCommission=" + sellCommission +
                ", executedMoney=" + executedMoney +
                ", buyPortfolioId=" + buyPortfolioId +
                ", sellPortfolioId=" + sellPortfolioId +
                ", buyUserId=" + buyUserId +
                ", sellUserId=" + sellUserId +
                '}';
    }
}
