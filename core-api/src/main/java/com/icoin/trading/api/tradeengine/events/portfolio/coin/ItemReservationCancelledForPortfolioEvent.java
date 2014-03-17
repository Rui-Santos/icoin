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

package com.icoin.trading.api.tradeengine.events.portfolio.coin;


import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.coin.events.CoinId;
import com.icoin.trading.api.tradeengine.events.portfolio.PortfolioId;
import com.icoin.trading.api.tradeengine.events.transaction.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class ItemReservationCancelledForPortfolioEvent extends EventSupport<ItemReservationCancelledForPortfolioEvent> {
    private PortfolioId portfolioIdentifier;
    private CoinId coinId;
    private TransactionId transactionIdentifier;
    private BigMoney leftTotalItem;
    private BigMoney leftCommission;
    private Date time;

    public ItemReservationCancelledForPortfolioEvent(PortfolioId portfolioIdentifier,
                                                     CoinId coinId,
                                                     TransactionId transactionIdentifier,
                                                     BigMoney leftTotalItem,
                                                     BigMoney leftCommission,
                                                     Date time) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.coinId = coinId;
        this.transactionIdentifier = transactionIdentifier;
        this.leftTotalItem = leftTotalItem;
        this.leftCommission = leftCommission;
        this.time = time;
    }

    public BigMoney getLeftTotalItem() {
        return leftTotalItem;
    }

    public BigMoney getLeftCommission() {
        return leftCommission;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public Date getTime() {
        return time;
    }
}
