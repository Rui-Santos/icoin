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

package com.icoin.trading.api.tradeengine.events.portfolio.cash;


import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.tradeengine.events.portfolio.PortfolioId;
import com.icoin.trading.api.tradeengine.events.transaction.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class CashReservationConfirmedEvent extends EventSupport<CashReservationConfirmedEvent> {
    private TransactionId transactionIdentifier;
    private PortfolioId portfolioIdentifier;
    private BigMoney amountOfMoney;
    private BigMoney commission;
    private Date time;

    public CashReservationConfirmedEvent(PortfolioId portfolioIdentifier,
                                         TransactionId transactionId,
                                         BigMoney amountOfMoney,
                                         BigMoney commission,
                                         Date time) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.transactionIdentifier = transactionId;
        this.amountOfMoney = amountOfMoney;
        this.commission = commission;
        this.time = time;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public BigMoney getAmountOfMoney() {
        return amountOfMoney;
    }

    public BigMoney getCommission() {
        return commission;
    }

    public Date getTime() {
        return time;
    }
}
