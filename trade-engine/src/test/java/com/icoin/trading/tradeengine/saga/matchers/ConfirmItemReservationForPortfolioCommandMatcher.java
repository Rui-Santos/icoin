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

import com.icoin.trading.api.tradeengine.command.portfolio.coin.ConfirmAmountReservationForPortfolioCommand;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import org.hamcrest.Description;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class ConfirmItemReservationForPortfolioCommandMatcher
        extends BaseCommandMatcher<ConfirmAmountReservationForPortfolioCommand> {

    private CoinId coinId;
    private PortfolioId portfolioIdentifier;
    private BigMoney amountOfConfirmedItem;

    public ConfirmItemReservationForPortfolioCommandMatcher(
            CoinId coinId, PortfolioId portfolioIdentifier, BigMoney amountOfConfirmedItem) {
        this.coinId = coinId;
        this.portfolioIdentifier = portfolioIdentifier;
        this.amountOfConfirmedItem = amountOfConfirmedItem;
    }

    @Override
    protected boolean doMatches(ConfirmAmountReservationForPortfolioCommand command) {
        return command.getCoinId().equals(coinId)
                && command.getPortfolioIdentifier().equals(portfolioIdentifier)
                && amountOfConfirmedItem.compareTo(command.getAmountOfItem()) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ConfirmAmountReservationForPortfolioCommand with amountOfConfirmedItem [")
                .appendValue(amountOfConfirmedItem)
                .appendText("] for CoinId with identifier [")
                .appendValue(coinId)
                .appendText("] and for Portfolio with identifier [")
                .appendValue(portfolioIdentifier)
                .appendText("]");
    }
}
