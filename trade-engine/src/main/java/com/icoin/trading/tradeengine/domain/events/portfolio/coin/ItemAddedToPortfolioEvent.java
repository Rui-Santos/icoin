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

package com.icoin.trading.tradeengine.domain.events.portfolio.coin;


import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import org.joda.money.BigMoney;

/**
 * New items have been added to the portfolio for the OrderBook of the provided identifier.
 *
 * @author Jettro Coenradie
 */
public class ItemAddedToPortfolioEvent {
    private PortfolioId portfolioIdentifier;
    private CoinId coinId;
    private BigMoney amountOfItemAdded;

    public ItemAddedToPortfolioEvent(PortfolioId portfolioIdentifier,
                                     CoinId coinId,
                                     BigMoney amountOfItemAdded) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.coinId = coinId;
        this.amountOfItemAdded = amountOfItemAdded;
    }

    public BigMoney getAmountOfItemAdded() {
        return amountOfItemAdded;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }
}
