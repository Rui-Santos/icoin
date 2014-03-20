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
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Try to add new items for a specific OrderBook to the portfolio.
 *
 * @author Jettro Coenradie
 */
public class AddAmountToPortfolioCommand extends CommandSupport<AddAmountToPortfolioCommand> {

    private PortfolioId portfolioIdentifier;
    private CoinId coinId;
    //    @DecimalMin("0.00000001")
    private BigMoney amountOfItemToAdd;
    @NotNull
    private Date time;

    /**
     * Create a new command.
     *
     * @param portfolioIdentifier Identifier of the Portfolio to add items to
     * @param coinId              Identifier of the CoinId to add items for
     * @param amountOfItemToAdd   AMount of items to add
     */
    public AddAmountToPortfolioCommand(PortfolioId portfolioIdentifier,
                                       CoinId coinId,
                                       BigMoney amountOfItemToAdd,
                                       Date time) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.coinId = coinId;
        this.amountOfItemToAdd = amountOfItemToAdd;
        this.time = time;
    }

    public BigMoney getAmountOfItemToAdd() {
        return amountOfItemToAdd;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "AddAmountToPortfolioCommand{" +
                "portfolioIdentifier=" + portfolioIdentifier +
                ", coinId=" + coinId +
                ", amountOfItemToAdd=" + amountOfItemToAdd +
                ", time=" + time +
                '}';
    }
}
