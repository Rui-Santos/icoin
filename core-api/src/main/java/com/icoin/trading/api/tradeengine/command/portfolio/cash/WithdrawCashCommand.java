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
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class WithdrawCashCommand extends CommandSupport<WithdrawCashCommand> {

    private PortfolioId portfolioIdentifier;
    //    @Min(0)
    private BigMoney amountToPay;

    private Date withdrawnTime;

    public WithdrawCashCommand(PortfolioId portfolioIdentifier, BigMoney amountToPay, Date withdrawnTime) {

        this.portfolioIdentifier = portfolioIdentifier;
        this.amountToPay = amountToPay;
        this.withdrawnTime = withdrawnTime;
    }

    public BigMoney getAmountToPay() {
        return amountToPay;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public Date getWithdrawnTime() {
        return withdrawnTime;
    }
}
