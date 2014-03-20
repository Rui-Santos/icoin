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

package com.icoin.trading.api.tradeengine.events.transaction;


import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public abstract class AbstractTransactionExecutedEvent<T extends AbstractTransactionExecutedEvent> extends EventSupport<T> {
    private TransactionId transactionIdentifier;
    private CoinId coinId;
    private BigMoney amountOfItem;
    private BigMoney itemPrice;
    private BigMoney executedMoney;
    private BigMoney commission;
    @NotNull
    private Date time;

    public AbstractTransactionExecutedEvent(TransactionId transactionIdentifier,
                                            CoinId coinId,
                                            BigMoney amountOfItem,
                                            BigMoney itemPrice,
                                            BigMoney executedMoney,
                                            BigMoney commission,
                                            Date time) {
        this.transactionIdentifier = transactionIdentifier;
        this.coinId = coinId;
        this.amountOfItem = amountOfItem;
        this.itemPrice = itemPrice;
        this.executedMoney = executedMoney;
        this.commission = commission;
        this.time = time;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public BigMoney getAmountOfItem() {
        return amountOfItem;
    }

    public BigMoney getItemPrice() {
        return itemPrice;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public BigMoney getCommission() {
        return commission;
    }

    public Date getTime() {
        return time;
    }
}
