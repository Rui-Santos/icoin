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
import org.mockito.ArgumentMatcher;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class PortfolioEntryMatcher extends ArgumentMatcher<PortfolioEntry> {
    private int itemsInPossession;
    private String itemIdentifier;
    private BigDecimal amountOfItemInPossession;
    private int itemsInReservation;
    private BigDecimal amountOfItemInReservation;
    private float min = 0.00000000001F;

    public PortfolioEntryMatcher(String itemIdentifier, int itemsInPossession, BigDecimal amountOfItemInPossession,
                                 int itemsInReservation, BigDecimal amountOfItemInReservation) {
        this.itemsInPossession = itemsInPossession;
        this.itemIdentifier = itemIdentifier;
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
                && Math.abs(amountOfItemInPossession.floatValue() -
                portfolioEntry.findItemInPossession(itemIdentifier).getAmount().floatValue()) < min
                && portfolioEntry.getItemsReserved().size() == itemsInReservation
                && !(itemsInReservation != 0 &&
                Math.abs(amountOfItemInReservation.floatValue() -
                        portfolioEntry.findReservedItemByIdentifier(itemIdentifier).getAmount().floatValue()) > min);
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
