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

import com.icoin.trading.tradeengine.application.command.order.CreateSellOrderCommand;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import org.hamcrest.Description;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class CreateSellOrderCommandMatcher extends BaseCommandMatcher<CreateSellOrderCommand> {

    private OrderBookId orderbookIdentifier;
    private PortfolioId portfolioIdentifier;
    private BigMoney tradeAmount;
    private BigMoney itemPrice;

    public CreateSellOrderCommandMatcher(PortfolioId portfolioId, OrderBookId orderbookId, BigMoney tradeAmount, BigMoney itemPrice) {
        this.portfolioIdentifier = portfolioId;
        this.orderbookIdentifier = orderbookId;
        this.tradeAmount = tradeAmount;
        this.itemPrice = itemPrice;
    }

    @Override
    protected boolean doMatches(CreateSellOrderCommand command) {
        return command.getOrderBookId().equals(orderbookIdentifier)
                && command.getPortfolioId().equals(portfolioIdentifier)
                && tradeAmount.compareTo(command.getTradeAmount()) == 0
                && itemPrice.compareTo(command.getItemPrice()) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("CreateSellOrderCommand with tradeAmount [")
                .appendValue(tradeAmount)
                .appendText("], itemPrice [")
                .appendValue(itemPrice)
                .appendText("] for OrderBook with identifier [")
                .appendValue(orderbookIdentifier)
                .appendText("] and for Portfolio with identifier [")
                .appendValue(portfolioIdentifier)
                .appendText("]");
    }
}
