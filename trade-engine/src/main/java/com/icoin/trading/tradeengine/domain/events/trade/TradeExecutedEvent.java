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


import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>A new trade has been executed. The event contains the amount of items that are traded and the price for the items
 * that are traded. The event also contains the identifiers for the Buy Order and the Sell order.</p>
 *
 * @author Allard Buijze
 */
public class TradeExecutedEvent implements Serializable {
    private static final long serialVersionUID = 6292249351659536792L;

    private final BigDecimal tradeAmount;
    private final BigDecimal tradePrice;
    private final OrderId buyOrderId;
    private final OrderId sellOrderId;
    private final TransactionId buyTransactionId;
    private final TransactionId sellTransactionId;
    private final OrderBookId orderBookId;

    public TradeExecutedEvent(OrderBookId orderBookId,
                              BigDecimal tradeAmount,
                              BigDecimal tradePrice,
                              OrderId buyOrderId,
                              OrderId sellOrderId,
                              TransactionId buyTransactionId,
                              TransactionId sellTransactionId) {
        this.tradeAmount = tradeAmount.setScale(8);
        this.tradePrice = tradePrice.setScale(8);
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.sellTransactionId = sellTransactionId;
        this.buyTransactionId = buyTransactionId;
        this.orderBookId = orderBookId;
    }

    public OrderBookId getOrderBookIdentifier() {
        return this.orderBookId;
    }

    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    public BigDecimal getTradePrice() {
        return tradePrice;
    }

    public OrderId getBuyOrderId() {
        return buyOrderId;
    }

    public OrderId getSellOrderId() {
        return sellOrderId;
    }

    public TransactionId getBuyTransactionId() {
        return buyTransactionId;
    }

    public TransactionId getSellTransactionId() {
        return sellTransactionId;
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
                .append(tradeAmount.setScale(8), other.tradeAmount.setScale(8))
                .append(tradePrice.setScale(8), other.tradePrice.setScale(8))
                .append(buyOrderId, other.buyOrderId)
                .append(sellOrderId, other.sellOrderId)
                .append(sellTransactionId, other.sellTransactionId)
                .append(buyTransactionId, other.buyTransactionId)
                .append(orderBookId, other.orderBookId)
                .build();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(tradeAmount.setScale(8))
                .append(tradePrice.setScale(8))
                .append(buyOrderId)
                .append(sellOrderId)
                .append(sellTransactionId)
                .append(buyTransactionId)
                .append(orderBookId)
                .build();
    }
}
