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

package com.icoin.trading.tradeengine.query.transaction;

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionType;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class TransactionEntry extends AuditAwareEntitySupport<TransactionEntry, String, Long> {
    private String orderbookIdentifier;
    private String portfolioIdentifier;

    private String coinName;
    private BigDecimal amountOfItems;
    private BigDecimal amountOfExecutedItems;
    private BigDecimal pricePerItem;
    private TransactionState state;
    private TransactionType type;

    public BigDecimal getAmountOfExecutedItems() {
        return amountOfExecutedItems;
    }

    public void setAmountOfExecutedItems(BigDecimal amountOfExecutedItems) {
        this.amountOfExecutedItems = amountOfExecutedItems;
    }

    public BigDecimal getAmountOfItems() {
        return amountOfItems;
    }

    public void setAmountOfItems(BigDecimal amountOfItems) {
        this.amountOfItems = amountOfItems;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getOrderbookIdentifier() {
        return orderbookIdentifier;
    }

    public void setOrderbookIdentifier(String orderbookIdentifier) {
        this.orderbookIdentifier = orderbookIdentifier;
    }

    public String getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public void setPortfolioIdentifier(String portfolioIdentifier) {
        this.portfolioIdentifier = portfolioIdentifier;
    }

    public BigDecimal getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(BigDecimal pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public TransactionState getState() {
        return state;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
