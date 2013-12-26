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

package com.icoin.trading.tradeengine.saga.matchers;

import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import org.hamcrest.Description;
import org.joda.money.BigMoney;


/**
 * @author Jettro Coenradie
 */
public class AddItemToPortfolioCommandMatcher extends BaseCommandMatcher<AddAmountToPortfolioCommand> {

    private CoinId coinId;
    private PortfolioId portfolioIdentifier;
    private BigMoney amountOfItemToAdd;

    public AddItemToPortfolioCommandMatcher(PortfolioId portfolioIdentifier,
                                            CoinId coinId,
                                            BigMoney amountOfItemToAdd) {
        this.amountOfItemToAdd = amountOfItemToAdd;
        this.portfolioIdentifier = portfolioIdentifier;
        this.coinId = coinId;
    }

    @Override
    protected boolean doMatches(AddAmountToPortfolioCommand command) {
        return command.getCoinId().equals(coinId)
                && command.getPortfolioIdentifier().equals(portfolioIdentifier)
                && command.getAmountOfItemToAdd().isEqual(amountOfItemToAdd);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("AddAmountToPortfolioCommand with amountOfItemToAdd [")
                .appendValue(amountOfItemToAdd)
                .appendText("] for Coin with identifier [")
                .appendValue(coinId)
                .appendText("] and for Portfolio with identifier [")
                .appendValue(portfolioIdentifier)
                .appendText("]");
    }
}
