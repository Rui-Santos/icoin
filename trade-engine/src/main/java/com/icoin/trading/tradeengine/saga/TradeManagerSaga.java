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

import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.joda.money.BigMoney;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jettro Coenradie
 */
public abstract class TradeManagerSaga extends AbstractAnnotatedSaga {

    private transient CommandBus commandBus;
    private transient CommissionPolicyFactory commissionPolicyFactory;
    private BigMoney totalItems;
    private BigMoney pricePerItem;
    private TransactionId transactionIdentifier;
    private OrderBookId orderbookIdentifier;
    private PortfolioId portfolioIdentifier;

    /*-------------------------------------------------------------------------------------------*/
    /* Getters and setters                                                                       */
    /*-------------------------------------------------------------------------------------------*/
    @Autowired
    public void setCommandBus(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Autowired
    public void setCommissionPolicyFactory(CommissionPolicyFactory commissionPolicyFactory) {
        this.commissionPolicyFactory = commissionPolicyFactory;
    }

    protected CommandBus getCommandBus() {
        return commandBus;
    }

    protected CommissionPolicyFactory getCommissionPolicyFactory() {
        return commissionPolicyFactory;
    }

    protected OrderBookId getOrderbookIdentifier() {
        return orderbookIdentifier;
    }

    protected void setOrderbookIdentifier(OrderBookId orderbookIdentifier) {
        this.orderbookIdentifier = orderbookIdentifier;
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

    protected BigMoney getTotalItems() {
        return totalItems;
    }

    protected void setTotalItems(BigMoney totalItems) {
        this.totalItems = totalItems;
    }

    protected TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    protected void setTransactionIdentifier(TransactionId transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }
}
