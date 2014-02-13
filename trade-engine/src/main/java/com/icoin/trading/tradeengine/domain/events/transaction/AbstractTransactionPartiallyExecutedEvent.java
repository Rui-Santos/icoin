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
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public abstract class AbstractTransactionPartiallyExecutedEvent<T extends AbstractTransactionPartiallyExecutedEvent> extends EventSupport<T> {
    private TransactionId transactionIdentifier;
    private CoinId coinId;
    private BigMoney amountOfExecutedItem;
    private BigMoney totalOfExecutedItem;
    private BigMoney itemPrice;
    private BigMoney executedMoney;
    private BigMoney commission;

    public AbstractTransactionPartiallyExecutedEvent(TransactionId transactionIdentifier,
                                                     CoinId coinId,
                                                     BigMoney amountOfExecutedItem,
                                                     BigMoney totalOfExecutedItem,
                                                     BigMoney itemPrice,
                                                     BigMoney executedMoney,
                                                     BigMoney commission) {
        this.transactionIdentifier = transactionIdentifier;
        this.coinId = coinId;
        this.amountOfExecutedItem = amountOfExecutedItem;
        this.totalOfExecutedItem = totalOfExecutedItem;
        this.itemPrice = itemPrice;
        this.executedMoney = executedMoney;
        this.commission = commission;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public BigMoney getAmountOfExecutedItem() {
        return amountOfExecutedItem;
    }

    public BigMoney getItemPrice() {
        return itemPrice;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public BigMoney getTotalOfExecutedItem() {
        return totalOfExecutedItem;
    }

    public BigMoney getCommission() {
        return commission;
    }
}
