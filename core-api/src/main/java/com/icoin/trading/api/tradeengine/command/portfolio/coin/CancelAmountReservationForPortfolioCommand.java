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
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Cancel a reservation for an amount of items for the OrderBook belonging to the provided identifier in the Portfolio
 * of the provided identifier.
 *
 * @author Jettro Coenradie
 */
public class CancelAmountReservationForPortfolioCommand extends CommandSupport<CancelAmountReservationForPortfolioCommand> {

    private PortfolioId portfolioIdentifier;
    private CoinId coinId;
    private TransactionId transactionIdentifier;
    private BigMoney leftTotalItem;
    private BigMoney leftCommission;
    @NotNull
    private Date time;

    public CancelAmountReservationForPortfolioCommand(PortfolioId portfolioIdentifier,
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
