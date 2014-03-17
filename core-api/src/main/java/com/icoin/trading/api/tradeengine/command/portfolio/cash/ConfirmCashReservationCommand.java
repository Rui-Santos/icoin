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

package com.icoin.trading.api.tradeengine.command.portfolio.cash;


import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.tradeengine.events.portfolio.PortfolioId;
import com.icoin.trading.api.tradeengine.events.transaction.TransactionId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class ConfirmCashReservationCommand extends CommandSupport<ConfirmCashReservationCommand> {

    private PortfolioId portfolioIdentifier;
    private TransactionId transactionIdentifier;
    private BigMoney amountOfMoney;
    private BigMoney commission;
    @NotNull
    private Date time;


    public ConfirmCashReservationCommand(PortfolioId portfolioIdentifier,
                                         TransactionId transactionIdentifier,
                                         BigMoney amountOfMoney,
                                         BigMoney commission,
                                         Date time) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.transactionIdentifier = transactionIdentifier;
        this.amountOfMoney = amountOfMoney;
        this.commission = commission;
        this.time = time;
    }

    public BigMoney getAmountOfMoney() {
        return amountOfMoney;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public BigMoney getCommission() {
        return commission;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "ConfirmCashReservationCommand{" +
                "portfolioIdentifier=" + portfolioIdentifier +
                ", transactionIdentifier=" + transactionIdentifier +
                ", amountOfMoney=" + amountOfMoney +
                ", commission=" + commission +
                ", time=" + time +
                '}';
    }
}
