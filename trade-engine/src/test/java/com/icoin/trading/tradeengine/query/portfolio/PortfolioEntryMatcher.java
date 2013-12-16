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

package com.icoin.trading.tradeengine.query.portfolio;

import org.hamcrest.Description;
import org.joda.money.BigMoney;
import org.mockito.ArgumentMatcher;

/**
 * @author Jettro Coenradie
 */
public class PortfolioEntryMatcher extends ArgumentMatcher<PortfolioEntry> {
    private int itemsInPossession;
    private String coinIdentifier;
    private BigMoney amountOfItemInPossession;
    private int itemsInReservation;
    private BigMoney amountOfItemInReservation;

    public PortfolioEntryMatcher(String coinIdentifier, int itemsInPossession, BigMoney amountOfItemInPossession,
                                 int itemsInReservation, BigMoney amountOfItemInReservation) {
        this.itemsInPossession = itemsInPossession;
        this.coinIdentifier = coinIdentifier;
        this.amountOfItemInPossession = amountOfItemInPossession;
        this.itemsInReservation = itemsInReservation;
        this.amountOfItemInReservation = amountOfItemInReservation;
    }

    @Override
    public boolean matches(Object argument) {
        if (!(argument instanceof PortfolioEntry)) {
            return false;
        }
        PortfolioEntry portfolioEntry = (PortfolioEntry) argument;

        return portfolioEntry.getItemsInPossession().size() == itemsInPossession
                && amountOfItemInPossession.minus(portfolioEntry.findItemInPossession(coinIdentifier).getAmount()).isNegativeOrZero()
                && portfolioEntry.getItemsReserved().size() == itemsInReservation
                && !(itemsInReservation != 0
                && amountOfItemInReservation.minus(portfolioEntry.findReservedItemByIdentifier(coinIdentifier).getAmount()).isPositive());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("PortfolioEntry with itemsInPossession [")
                .appendValue(itemsInPossession)
                .appendText("] and amountOfItemsInPossession [")
                .appendValue(amountOfItemInPossession)
                .appendText("] and amountOfItemsInReservation [")
                .appendValue(amountOfItemInReservation)
                .appendText("] and itemsInReservation [")
                .appendValue(itemsInReservation)
                .appendText("]");
    }
}
