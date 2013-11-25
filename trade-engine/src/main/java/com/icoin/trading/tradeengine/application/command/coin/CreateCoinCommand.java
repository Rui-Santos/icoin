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


import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.user.UserId;

/**
 * <p>Create a new company by proving the name, the estimated value of the company and the amount of shares that are
 * available for the company. You also must provide the id of the user that wants to create the company.</p>
 *
 * @author Jettro Coenradie
 */
public class CreateCoinCommand {
    private CoinId coinId;
    private UserId userId;
    private String coinName;
    private long coinInitialPrice;
    private long coinInitialAmount;

    public CreateCoinCommand(CoinId coinId, UserId userId, String coinName, long coinInitialPrice, long coinInitialAmount) {
        this.coinId = coinId;
        this.coinInitialAmount = coinInitialAmount;
        this.coinName = coinName;
        this.coinInitialPrice = coinInitialPrice;
        this.userId = userId;
    }

    public long getCoinInitialAmount() {
        return coinInitialAmount;
    }

    public String getCoinName() {
        return coinName;
    }

    public long getCoinInitialPrice() {
        return coinInitialPrice;
    }

    public UserId getUserId() {
        return userId;
    }

    public CoinId getCoinId() {
        return coinId;
    }
}
