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

package com.icoin.trading.tradeengine.saga;

import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.joda.money.BigMoney;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jettro Coenradie
 */
public abstract class TradeManagerSaga extends AbstractAnnotatedSaga {

    private transient CommandGateway commandGateway;
    private TransactionId transactionIdentifier;
    private OrderBookId orderBookIdentifier;
    private PortfolioId portfolioIdentifier;
    private CoinId coinId;
    private BigMoney totalItem;
    private BigMoney pricePerItem;
    private BigMoney totalCommission;
    private BigMoney leftCommission;

    /*-------------------------------------------------------------------------------------------*/
    /* Getters and setters                                                                       */
    /*-------------------------------------------------------------------------------------------*/
    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    protected CommandGateway getCommandGateway() {
        return commandGateway;
    }

    protected OrderBookId getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    protected void setOrderBookIdentifier(OrderBookId orderBookIdentifier) {
        this.orderBookIdentifier = orderBookIdentifier;
    }

    protected CoinId getCoinId() {
        return coinId;
    }

    protected void setCoinId(CoinId coinId) {
        this.coinId = coinId;
    }

    protected PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    protected void setPortfolioIdentifier(PortfolioId portfolioIdentifier) {
        this.portfolioIdentifier = portfolioIdentifier;
    }

    protected BigMoney getPricePerItem() {
        return pricePerItem;
    }

    protected void setPricePerItem(BigMoney pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    protected BigMoney getTotalItem() {
        return totalItem;
    }

    protected BigMoney getTotalCommission() {
        return totalCommission;
    }

    protected void setTotalCommission(BigMoney totalCommission) {
        this.totalCommission = totalCommission;
    }

    protected void setTotalItem(BigMoney totalItems) {
        this.totalItem = totalItems;
    }

    protected BigMoney getLeftCommission() {
        return leftCommission;
    }

    protected void setLeftCommission(BigMoney leftCommission) {
        this.leftCommission = leftCommission;
    }

    protected TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    protected void setTransactionIdentifier(TransactionId transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    //adjust commission, total commission should be equal to the sum up of the commission.
    protected BigMoney adjustCommission(BigMoney executedCommission) {
        BigMoney zero = BigMoney.zero(leftCommission.getCurrencyUnit());

        if (leftCommission.isNegative()) {
            leftCommission = zero;
            return zero;
        }

        BigMoney left = leftCommission.minus(executedCommission);
        if (left.isNegative()) {
            BigMoney adjusted = leftCommission;
            leftCommission = zero;
            return adjusted;
        }

        leftCommission = left;
        return executedCommission;
    }
}
