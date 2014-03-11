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

package com.icoin.trading.tradeengine.application.command.portfolio.coin;


import com.homhon.base.command.CommandSupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class ReserveAmountCommand extends CommandSupport<ReserveAmountCommand> {

    private PortfolioId portfolioIdentifier;
    private TransactionId transactionIdentifier;
    private CoinId coinId;
    @NotNull
    private BigMoney amountOfItemToReserve;
    private BigMoney commission;
    @NotNull
    private Date time;

    public ReserveAmountCommand(PortfolioId portfolioIdentifier,
                                CoinId coinId,
                                TransactionId transactionIdentifier,
                                BigMoney amountOfItemToReserve,
                                BigMoney commission,
                                Date time) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.coinId = coinId;
        this.transactionIdentifier = transactionIdentifier;
        this.amountOfItemToReserve = amountOfItemToReserve;
        this.commission = commission;
        this.time = time;
    }

    public BigMoney getAmountOfItemToReserve() {
        return amountOfItemToReserve;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public CoinId getCoinId() {
        return coinId;
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
}
