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
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class TransactionEntry extends AuditAwareEntitySupport<TransactionEntry, String, Long> {
    private String orderBookIdentifier;
    private String portfolioIdentifier;

    //    private String coinName;
    private BigMoney amountOfItem;
    private BigMoney totalCommission;
    private BigMoney totalMoney;
    private TransactionState state;
    private BigMoney amountOfExecutedItem;
    private BigMoney executedMoney;
    private BigMoney commission;
    private TransactionType type;

    public TransactionEntry addCommission(BigMoney commission) {
        if (this.commission == null) {
            this.commission = commission;
            return this;
        }

        this.commission = this.commission.plus(commission);
        return this;
    }

    public TransactionEntry addExecutedMoney(BigMoney executedMoney) {
        if (this.executedMoney == null) {
            this.executedMoney = executedMoney;
            return this;
        }

        this.executedMoney = this.executedMoney.plus(executedMoney);
        return this;
    }

    public TransactionEntry addAmountOfExecutedItem(BigMoney amountOfExecutedItem) {
        if (this.amountOfExecutedItem == null) {
            this.amountOfExecutedItem = amountOfExecutedItem;
            return this;
        }

        this.amountOfExecutedItem = this.amountOfExecutedItem.plus(amountOfExecutedItem);
        return this;
    }

    public BigMoney getAmountOfExecutedItem() {
        return amountOfExecutedItem;
    }

    public void setAmountOfExecutedItem(BigMoney amountOfExecutedItem) {
        this.amountOfExecutedItem = amountOfExecutedItem;
    }

    public BigMoney getAmountOfItem() {
        return amountOfItem;
    }

    public void setAmountOfItem(BigMoney amountOfItem) {
        this.amountOfItem = amountOfItem;
    }

//    public String getCoinName() {
//        return coinName;
//    }
//
//    public void setCoinName(String coinName) {
//        this.coinName = coinName;
//    }

    public String getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public void setOrderBookIdentifier(String orderBookIdentifier) {
        this.orderBookIdentifier = orderBookIdentifier;
    }

    public String getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public void setPortfolioIdentifier(String portfolioIdentifier) {
        this.portfolioIdentifier = portfolioIdentifier;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public void setExecutedMoney(BigMoney executedMoney) {
        this.executedMoney = executedMoney;
    }

    public BigMoney getCommission() {
        return commission;
    }

    public void setCommission(BigMoney commission) {
        this.commission = commission;
    }

    public BigMoney getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigMoney totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigMoney getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigMoney totalCommission) {
        this.totalCommission = totalCommission;
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
