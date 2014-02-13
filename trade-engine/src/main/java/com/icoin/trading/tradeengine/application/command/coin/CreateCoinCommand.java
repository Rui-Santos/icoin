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

package com.icoin.trading.tradeengine.application.command.coin;


import com.homhon.base.command.CommandSupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import org.joda.money.BigMoney;

/**
 * <p>Create a new coin by proving the name, the estimated value of the coin and the amount of shares that are
 * available for the coin. You also must provide the id of the user that wants to create the coin.</p>
 *
 * @author Jettro Coenradie
 */
public class CreateCoinCommand extends CommandSupport<CreateCoinCommand> {
    private CoinId coinId;
    private String coinName;
    private BigMoney coinInitialPrice;
    private BigMoney coinInitialAmount;

    public CreateCoinCommand(CoinId coinId, String coinName, BigMoney coinInitialPrice, BigMoney coinInitialAmount) {
        this.coinId = coinId;
        this.coinInitialAmount = coinInitialAmount;
        this.coinName = coinName;
        this.coinInitialPrice = coinInitialPrice;
    }

    public BigMoney getCoinInitialAmount() {
        return coinInitialAmount;
    }

    public String getCoinName() {
        return coinName;
    }

    public BigMoney getCoinInitialPrice() {
        return coinInitialPrice;
    }

    public CoinId getCoinId() {
        return coinId;
    }
}
