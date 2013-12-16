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

package com.icoin.trading.tradeengine.domain.events.trade;


import com.icoin.trading.tradeengine.domain.model.order.TradeType;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.money.BigMoney;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.Date;

/**
 * <p>A new trade has been executed. The event contains the amount of items that are traded and the price for the items
 * that are traded. The event also contains the identifiers for the Buy Order and the Sell order.</p>
 *
 * @author Allard Buijze
 */
public class TradeExecutedEvent implements Serializable {
    private static final long serialVersionUID = 6292249351659536792L;

    private final BigMoney tradeAmount;
    private final BigMoney tradedPrice;
    private final String buyOrderId;
    private final String sellOrderId;
    private final TransactionId buyTransactionId;
    private final TransactionId sellTransactionId;
    private final OrderBookId orderBookId;
    private final Date tradeTime;
    private final TradeType tradeType;

    public TradeExecutedEvent(OrderBookId orderBookId,
                              BigMoney tradeAmount,
                              BigMoney tradedPrice,
                              String buyOrderId,
                              String sellOrderId,
                              TransactionId buyTransactionId,
                              TransactionId sellTransactionId,
                              Date tradeTime,
                              TradeType tradeType) {
        this.tradeAmount = tradeAmount;
        this.tradedPrice = tradedPrice;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.sellTransactionId = sellTransactionId;
        this.buyTransactionId = buyTransactionId;
        this.orderBookId = orderBookId;
        this.tradeTime = tradeTime;
        this.tradeType = tradeType;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) {
            return false;
        }

        if (!TradeExecutedEvent.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        TradeExecutedEvent other = (TradeExecutedEvent) obj;

        return new EqualsBuilder()
                .append(tradeAmount.toMoney(RoundingMode.HALF_EVEN), other.tradeAmount.toMoney(RoundingMode.HALF_EVEN))
                .append(tradedPrice.toMoney(RoundingMode.HALF_EVEN), other.tradedPrice.toMoney(RoundingMode.HALF_EVEN))
                .append(buyOrderId, other.buyOrderId)
                .append(sellOrderId, other.sellOrderId)
                .append(sellTransactionId, other.sellTransactionId)
                .append(buyTransactionId, other.buyTransactionId)
                .append(orderBookId, other.orderBookId)
                .append(tradeType, other.tradeType)
                .build();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(tradeAmount.toMoney(RoundingMode.HALF_EVEN))
                .append(tradedPrice.toMoney(RoundingMode.HALF_EVEN))
                .append(tradeType)
                .append(buyOrderId)
                .append(sellOrderId)
                .append(sellTransactionId)
                .append(buyTransactionId)
                .append(orderBookId)
                .build();
    }
}
