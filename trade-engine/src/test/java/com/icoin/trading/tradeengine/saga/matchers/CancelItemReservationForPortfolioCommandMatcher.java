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
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import org.hamcrest.Description;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class CancelItemReservationForPortfolioCommandMatcher
        extends BaseCommandMatcher<CancelAmountReservationForPortfolioCommand> {

    private OrderBookId orderBookIdentifier;
    private PortfolioId portfolioIdentifier;
    private BigMoney amountOfItemToCancel;

    public CancelItemReservationForPortfolioCommandMatcher(OrderBookId orderBookIdentifier,
                                                           PortfolioId portfolioIdentifier,
                                                           BigMoney amountOfItemToCancel) {
        this.amountOfItemToCancel = amountOfItemToCancel;
        this.portfolioIdentifier = portfolioIdentifier;
        this.orderBookIdentifier = orderBookIdentifier;
    }

    @Override
    protected boolean doMatches(CancelAmountReservationForPortfolioCommand command) {
        return command.getOrderBookIdentifier().equals(orderBookIdentifier)
                && command.getPortfolioIdentifier().equals(portfolioIdentifier)
                && command.getAmountOfItemsToCancel().compareTo(amountOfItemToCancel) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("CancelAmountReservationForPortfolioCommand with amountOfItemToCancel [")
                .appendValue(amountOfItemToCancel)
                .appendText("] for Portfolio with identifier [")
                .appendValue(portfolioIdentifier)
                .appendText("] and for OrderBook with identifier [")
                .appendValue(orderBookIdentifier)
                .appendText("]");
    }
}
