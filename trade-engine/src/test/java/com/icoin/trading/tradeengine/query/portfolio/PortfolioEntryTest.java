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

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author Jettro Coenradie
 */
public class PortfolioEntryTest {

    private static final BigDecimal AMOUNT_ITEMS = BigDecimal.valueOf(100);
    private static final BigDecimal AMOUNT_RESERVED = BigDecimal.valueOf(40);
    private static final BigDecimal AMOUNT_SELL = BigDecimal.TEN;
    private static final String ORDERBOOK_IDENTIFIER = "item1";
    private static final BigDecimal AMOUNT_OF_MONEY = BigDecimal.valueOf(1000);
    private static final BigDecimal RESERVED_AMOUNT_OF_MONEY = BigDecimal.valueOf(200);

    @Test
    public void testRemovingItems() {
        PortfolioEntry portfolio = createDefaultPortfolio();

        portfolio.removeReservedItem(ORDERBOOK_IDENTIFIER, AMOUNT_SELL);
        portfolio.removeItemsInPossession(ORDERBOOK_IDENTIFIER, AMOUNT_SELL);

        assertEquals(AMOUNT_RESERVED.subtract(AMOUNT_SELL),
                portfolio.findReservedItemByIdentifier(ORDERBOOK_IDENTIFIER).getAmount());
        assertEquals(AMOUNT_ITEMS.subtract(AMOUNT_SELL), portfolio.findItemInPossession(ORDERBOOK_IDENTIFIER).getAmount());
    }

    @Test
    public void testObtainAvailableItems() {
        PortfolioEntry portfolio = createDefaultPortfolio();

        assertEquals(AMOUNT_ITEMS.subtract(AMOUNT_RESERVED), portfolio.obtainAmountOfAvailableItemsFor(ORDERBOOK_IDENTIFIER));
    }

    @Test
    public void testObtainBudget() {
        PortfolioEntry portfolio = createDefaultPortfolio();
        assertEquals(AMOUNT_OF_MONEY.subtract(RESERVED_AMOUNT_OF_MONEY), portfolio.obtainMoneyToSpend());
    }

    private PortfolioEntry createDefaultPortfolio() {
        PortfolioEntry portfolio = new PortfolioEntry();

        portfolio.addItemInPossession(createItem(AMOUNT_ITEMS));
        portfolio.addReservedItem(createItem(AMOUNT_RESERVED));
        portfolio.setAmountOfMoney(AMOUNT_OF_MONEY);
        portfolio.setReservedAmountOfMoney(RESERVED_AMOUNT_OF_MONEY);
        return portfolio;
    }

    private ItemEntry createItem(BigDecimal amount) {
        ItemEntry item1InPossession = new ItemEntry();
        item1InPossession.setPrimaryKey("item1");
        item1InPossession.setAmount(amount);
        item1InPossession.setCoinIdentifier("coin1");
        item1InPossession.setCoinName("Coin One");
        return item1InPossession;
    }
}
