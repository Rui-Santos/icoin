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

import com.icoin.trading.api.portfolio.cash.DepositCashCommand;
import com.icoin.trading.api.orders.trades.PortfolioId;
import org.hamcrest.Description;

/**
 * @author Jettro Coenradie
 */
public class DepositMoneyToPortfolioCommandMatcher extends BaseCommandMatcher<DepositCashCommand> {

    private long moneyToAddInCents;
    private PortfolioId portfolioIdentifier;

    public DepositMoneyToPortfolioCommandMatcher(PortfolioId portfolioIdentifier, long moneyToAddInCents) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.moneyToAddInCents = moneyToAddInCents;
    }

    @Override
    protected boolean doMatches(DepositCashCommand command) {
        return moneyToAddInCents == command.getMoneyToAddInCents()
                && portfolioIdentifier.equals(command.getPortfolioIdentifier());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("DepositCashCommand with moneyToAddInCents [")
                .appendValue(moneyToAddInCents)
                .appendText("] for Portfolio with identifier [")
                .appendValue(portfolioIdentifier)
                .appendText("]");
    }
}
