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

package com.icoin.trading.api.tradeengine.command.portfolio.coin;


import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.tradeengine.events.transaction.TransactionId;
import com.icoin.trading.api.coin.events.CoinId;
import com.icoin.trading.api.tradeengine.events.portfolio.PortfolioId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Confirm the reserved items belonging to OrderBook of the provided identifier for the Portfolio of the provided
 * identifier.
 *
 * @author Jettro Coenradie
 */
public class ConfirmAmountReservationForPortfolioCommand extends CommandSupport<ConfirmAmountReservationForPortfolioCommand> {

    private PortfolioId portfolioIdentifier;
    private CoinId coinId;
    private TransactionId transactionIdentifier;
    private BigMoney amountOfItem;
    private BigMoney commission;
    @NotNull
    private Date time;

    public ConfirmAmountReservationForPortfolioCommand(PortfolioId portfolioIdentifier,
                                                       CoinId coinId,
                                                       TransactionId transactionIdentifier,
                                                       BigMoney amountOfItem,
                                                       BigMoney commission,
                                                       Date time) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.coinId = coinId;
        this.transactionIdentifier = transactionIdentifier;
        this.amountOfItem = amountOfItem;
        this.commission = commission;
        this.time = time;
    }

    public BigMoney getAmountOfItem() {
        return amountOfItem;
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

    public BigMoney getCommission() {
        return commission;
    }

    public Date getTime() {
        return time;
    }
}
