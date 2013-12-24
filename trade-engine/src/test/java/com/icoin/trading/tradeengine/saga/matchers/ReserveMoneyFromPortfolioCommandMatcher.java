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

import com.icoin.trading.tradeengine.application.command.portfolio.cash.ReserveCashCommand;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import org.hamcrest.Description;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class ReserveMoneyFromPortfolioCommandMatcher extends BaseCommandMatcher<ReserveCashCommand> {

    private PortfolioId portfolioIdentifier;
    private BigMoney amountOfMoneyToReserve;

    public ReserveMoneyFromPortfolioCommandMatcher(PortfolioId portfolioIdentifier, BigMoney amountOfMoneyToReserve) {
        this.amountOfMoneyToReserve = amountOfMoneyToReserve;
        this.portfolioIdentifier = portfolioIdentifier;
    }

    @Override
    protected boolean doMatches(ReserveCashCommand command) {
        return command.getPortfolioIdentifier().equals(portfolioIdentifier)
                && command.getAmountOfMoneyToReserve().isEqual(amountOfMoneyToReserve);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ReserveCashCommand with amountOfMoneyToReserve [")
                .appendValue(amountOfMoneyToReserve)
                .appendText("] for Portfolio with identifier [")
                .appendValue(portfolioIdentifier)
                .appendText("]");
    }
}