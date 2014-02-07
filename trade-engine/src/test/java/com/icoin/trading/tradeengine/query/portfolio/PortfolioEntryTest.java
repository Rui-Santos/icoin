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
import static org.junit.Assert.assertTrue;

/**
 * @author Jettro Coenradie
 */
public class PortfolioEntryTest {

    private static final BigMoney AMOUNT_ITEM = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100));
    private static final BigMoney AMOUNT_RESERVED = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(40));
    private static final BigMoney AMOUNT_SELL = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10));
    private static final String COIN_IDENTIFIER = "BTC";
    private static final BigMoney AMOUNT_OF_MONEY = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(1000));
    private static final BigMoney RESERVED_AMOUNT_OF_MONEY = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(200));

    @Test
    public void testReserve() {
        PortfolioEntry portfolio = createDefaultPortfolio();

        //assertion
        assertEquals(AMOUNT_ITEM,
                portfolio.obtainAmountOfItemInPossessionFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(AMOUNT_RESERVED,
                portfolio.obtainAmountOfReservedItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(AMOUNT_ITEM.minus(AMOUNT_RESERVED),
                portfolio.obtainAmountOfAvailableItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));

        //confirm reservation
        portfolio.confirmReserved(COIN_IDENTIFIER, AMOUNT_RESERVED);

        //assertion
        assertEquals(AMOUNT_ITEM.minus(AMOUNT_RESERVED),
                portfolio.obtainAmountOfItemInPossessionFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(BigMoney.zero(CurrencyUnit.of(COIN_IDENTIFIER)),
                portfolio.obtainAmountOfReservedItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(AMOUNT_ITEM.minus(AMOUNT_RESERVED),
                portfolio.obtainAmountOfAvailableItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
    }

    @Test
    public void testCancel() {
        PortfolioEntry portfolio = createDefaultPortfolio();

        //assertion
        assertEquals(AMOUNT_ITEM,
                portfolio.obtainAmountOfItemInPossessionFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(AMOUNT_RESERVED,
                portfolio.obtainAmountOfReservedItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(AMOUNT_ITEM.minus(AMOUNT_RESERVED),
                portfolio.obtainAmountOfAvailableItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));

        //confirm reservation
        portfolio.confirmReserved(COIN_IDENTIFIER, AMOUNT_RESERVED.minus(2));

        //assertion
        assertEquals(AMOUNT_ITEM.minus(AMOUNT_RESERVED).plus(2),
                portfolio.obtainAmountOfItemInPossessionFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(BigMoney.of(CurrencyUnit.of(COIN_IDENTIFIER), 2),
                portfolio.obtainAmountOfReservedItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertTrue(AMOUNT_ITEM.minus(AMOUNT_RESERVED).isEqual(portfolio.obtainAmountOfAvailableItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER))));

        //confirm reservation
        portfolio.cancelReserved(COIN_IDENTIFIER, BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1)));

        //assertion
        assertEquals(AMOUNT_ITEM.minus(AMOUNT_RESERVED).plus(2),
                portfolio.obtainAmountOfItemInPossessionFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(BigMoney.of(CurrencyUnit.of(COIN_IDENTIFIER), 1),
                portfolio.obtainAmountOfReservedItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
        assertEquals(AMOUNT_ITEM.minus(AMOUNT_RESERVED).plus(1),
                portfolio.obtainAmountOfAvailableItemFor(COIN_IDENTIFIER, CurrencyUnit.of(COIN_IDENTIFIER)));
    }

    @Test
    public void testObtainAvailableItems() {
        PortfolioEntry portfolio = createDefaultPortfolio();

        final BigMoney bigMoney = portfolio.obtainAmountOfAvailableItemFor(COIN_IDENTIFIER, AMOUNT_ITEM.getCurrencyUnit());

        portfolio.addItemInPossession(COIN_IDENTIFIER, bigMoney);

        assertEquals(bigMoney.multipliedBy(2),
                portfolio.obtainAmountOfAvailableItemFor(COIN_IDENTIFIER, AMOUNT_ITEM.getCurrencyUnit()));
    }

    @Test
    public void testObtainAmountOfAvailableItemFor() {
        PortfolioEntry portfolio = createDefaultPortfolio();

        assertEquals(AMOUNT_ITEM.minus(AMOUNT_RESERVED),
                portfolio.obtainAmountOfAvailableItemFor(COIN_IDENTIFIER, AMOUNT_ITEM.getCurrencyUnit()));
    }

    @Test
    public void testObtainBudget() {
        PortfolioEntry portfolio = createDefaultPortfolio();
        assertEquals(AMOUNT_OF_MONEY.minus(RESERVED_AMOUNT_OF_MONEY), portfolio.obtainMoneyToSpend());
    }

    private PortfolioEntry createDefaultPortfolio() {
        PortfolioEntry portfolio = new PortfolioEntry();

        portfolio.createItem(COIN_IDENTIFIER, "Bitcoin");

        portfolio.addItemInPossession(COIN_IDENTIFIER, AMOUNT_ITEM);
        portfolio.addReserved(COIN_IDENTIFIER, AMOUNT_RESERVED);
        portfolio.setAmountOfMoney(AMOUNT_OF_MONEY);
        portfolio.setReservedAmountOfMoney(RESERVED_AMOUNT_OF_MONEY);
        return portfolio;
    }
}
