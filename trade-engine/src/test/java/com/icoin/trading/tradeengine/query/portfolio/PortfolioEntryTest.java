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

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author Jettro Coenradie
 */
public class PortfolioEntryTest {

    private static final BigMoney AMOUNT_ITEMS = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100));
    private static final BigMoney AMOUNT_RESERVED = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(40));
    private static final BigMoney AMOUNT_SELL = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10));
    private static final String COIN_IDENTIFIER = "coin1";
    private static final BigMoney AMOUNT_OF_MONEY = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(1000));
    private static final BigMoney RESERVED_AMOUNT_OF_MONEY = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(200));

    @Test
    public void testRemovingItems() {
        PortfolioEntry portfolio = createDefaultPortfolio();

        portfolio.removeReservedItem(COIN_IDENTIFIER, AMOUNT_SELL);
        portfolio.removeItemsInPossession(COIN_IDENTIFIER, AMOUNT_SELL);

        assertEquals(AMOUNT_RESERVED.minus(AMOUNT_SELL),
                portfolio.findReservedItemByIdentifier(COIN_IDENTIFIER).getAmount());
        assertEquals(AMOUNT_ITEMS.minus(AMOUNT_SELL), portfolio.findItemInPossession(COIN_IDENTIFIER).getAmount());
    }

    @Test
    public void testObtainAvailableItems() {
        PortfolioEntry portfolio = createDefaultPortfolio();

        assertEquals(AMOUNT_ITEMS.minus(AMOUNT_RESERVED),
                portfolio.obtainAmountOfAvailableItemsFor(COIN_IDENTIFIER, AMOUNT_ITEMS.getCurrencyUnit()));
    }

    @Test
    public void testObtainBudget() {
        PortfolioEntry portfolio = createDefaultPortfolio();
        assertEquals(AMOUNT_OF_MONEY.minus(RESERVED_AMOUNT_OF_MONEY), portfolio.obtainMoneyToSpend());
    }

    private PortfolioEntry createDefaultPortfolio() {
        PortfolioEntry portfolio = new PortfolioEntry();

        portfolio.addItemInPossession(createItem(AMOUNT_ITEMS));
        portfolio.addReservedItem(createItem(AMOUNT_RESERVED));
        portfolio.setAmountOfMoney(AMOUNT_OF_MONEY);
        portfolio.setReservedAmountOfMoney(RESERVED_AMOUNT_OF_MONEY);
        return portfolio;
    }

    private ItemEntry createItem(BigMoney amount) {
        ItemEntry item1InPossession = new ItemEntry();
        item1InPossession.setAmount(amount);
        item1InPossession.setCoinIdentifier(COIN_IDENTIFIER);
        item1InPossession.setCoinName("Coin One");
        return item1InPossession;
    }
}
