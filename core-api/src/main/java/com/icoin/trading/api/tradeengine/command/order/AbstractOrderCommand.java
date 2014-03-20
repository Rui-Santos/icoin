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

package com.icoin.trading.api.tradeengine.command.order;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.OrderId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * <p>Abstract parent class for all commands that are order related.</p>
 *
 * @author Allard Buijze
 */
public abstract class AbstractOrderCommand<T extends AbstractOrderCommand> extends CommandSupport<T> {

    private PortfolioId portfolioId;
    @TargetAggregateIdentifier
    private OrderBookId orderBookId;
    private TransactionId transactionId;
    //    @DecimalMin("0.00000001")
    private BigMoney tradeAmount;
    //    @DecimalMin("0.00000001")
    private BigMoney itemPrice;

    private OrderId orderId;
    private Date placeDate;

    protected AbstractOrderCommand(OrderId orderId,
                                   PortfolioId portfolioId,
                                   OrderBookId orderBookId,
                                   TransactionId transactionId,
                                   BigMoney tradeAmount,
                                   BigMoney itemPrice,
                                   Date placeDate) {
        this.portfolioId = portfolioId;
        this.orderBookId = orderBookId;
        this.tradeAmount = tradeAmount;
        this.itemPrice = itemPrice;
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.placeDate = placeDate;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public TransactionId getTransactionId() {
        return transactionId;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public BigMoney getItemPrice() {
        return itemPrice;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Date getPlaceDate() {
        return placeDate;
    }
}
