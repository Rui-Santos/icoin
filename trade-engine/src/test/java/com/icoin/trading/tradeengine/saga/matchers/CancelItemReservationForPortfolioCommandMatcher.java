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

import com.icoin.trading.tradeengine.application.command.portfolio.coin.CancelAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import org.hamcrest.Description;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class CancelItemReservationForPortfolioCommandMatcher
        extends BaseCommandMatcher<CancelAmountReservationForPortfolioCommand> {

    private CoinId coinId;
    private PortfolioId portfolioIdentifier;
    private BigMoney amountOfItemToCancel;

    public CancelItemReservationForPortfolioCommandMatcher(CoinId coinId,
                                                           PortfolioId portfolioIdentifier,
                                                           BigMoney amountOfItemToCancel) {
        this.amountOfItemToCancel = amountOfItemToCancel;
        this.portfolioIdentifier = portfolioIdentifier;
        this.coinId = coinId;
    }

    @Override
    protected boolean doMatches(CancelAmountReservationForPortfolioCommand command) {
        return command.getCoinId().equals(coinId)
                && command.getPortfolioIdentifier().equals(portfolioIdentifier)
                && command.getLeftCommission().compareTo(amountOfItemToCancel) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("CancelAmountReservationForPortfolioCommand with amountOfItemToCancel [")
                .appendValue(amountOfItemToCancel)
                .appendText("] for Portfolio with identifier [")
                .appendValue(portfolioIdentifier)
                .appendText("] and for Coin with identifier [")
                .appendValue(coinId)
                .appendText("]");
    }
}
