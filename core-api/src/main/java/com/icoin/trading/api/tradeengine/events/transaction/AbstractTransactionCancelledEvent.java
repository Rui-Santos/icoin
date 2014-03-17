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
import com.icoin.trading.api.coin.events.CoinId;
import com.icoin.trading.api.tradeengine.events.transaction.TransactionId;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public abstract class AbstractTransactionCancelledEvent<T extends AbstractTransactionCancelledEvent> extends EventSupport<T> {
    private TransactionId transactionIdentifier;
    private CoinId coinId;
    @NotNull
    private Date time;

    public AbstractTransactionCancelledEvent(TransactionId transactionIdentifier,
                                             CoinId coinId,
                                             Date time) {
        this.transactionIdentifier = transactionIdentifier;
        this.coinId = coinId;
        this.time = time;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public Date getTime() {
        return time;
    }
}
