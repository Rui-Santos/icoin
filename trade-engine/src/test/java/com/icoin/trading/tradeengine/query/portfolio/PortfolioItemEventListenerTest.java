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
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.domain.model.user.UserId;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * We setup this test with a default portfolio and a default orderBook. The portfolio contains the default amount of
 * items in Reservation. This means that all available items are reserved.
 *
 * @author Jettro Coenradie
 */
public class PortfolioItemEventListenerTest {

    public static final BigMoney DEFAULT_AMOUNT_ITEM = BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(100));
    public static final BigMoney DEFAULT_AMOUNT_COMMISSION = BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(1));
    private PortfolioQueryRepository portfolioQueryRepository;
    private PortfolioItemEventListener listener;

    final UserId userIdentifier = new UserId();
    final PortfolioId portfolioIdentifier = new PortfolioId();
    final CoinId coinIdentifier = new CoinId("LTC");
    final TransactionId transactionIdentifier = new TransactionId();
    PortfolioEntry portfolioEntry;

    @Before
    public void setUp() throws Exception {
        portfolioQueryRepository = mock(PortfolioQueryRepository.class);

//        OrderBookQueryRepository orderBookQueryRepository = Mockito.mock(OrderBookQueryRepository.class);
//        OrderBookEntry orderBookEntry = createOrderBookEntry();
//        Mockito.when(orderBookQueryRepository.findOne(itemIdentifier.toString())).thenReturn(orderBookEntry);

        final CoinQueryRepository coinQueryRepository = mock(CoinQueryRepository.class);
        final CoinEntry coinEntry = new CoinEntry();
        coinEntry.setPrimaryKey(coinIdentifier.toString());
        coinEntry.setName("test");
        when(coinQueryRepository.findOne(eq(coinIdentifier.toString()))).thenReturn(coinEntry);

        listener = new PortfolioItemEventListener();
        listener.setPortfolioRepository(portfolioQueryRepository);
        listener.setCoinQueryRepository(coinQueryRepository);


        portfolioEntry = createPortfolioEntry();
        Mockito.when(portfolioQueryRepository.findOne(portfolioIdentifier.toString())).thenReturn(portfolioEntry);
    }

    @Test
    public void testHandleEventAddItems() throws Exception {
        Date time = currentTime();
        ItemAddedToPortfolioEvent event =
                new ItemAddedToPortfolioEvent(
                        portfolioIdentifier,
                        coinIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(100)),
                        time);

        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(
                new PortfolioEntryMatcher(
                        coinIdentifier.toString(),
                        DEFAULT_AMOUNT_ITEM.multipliedBy(BigDecimal.valueOf(2)),
                        BigMoney.zero(CurrencyUnit.of(Currencies.LTC)))));
    }

    @Test
    public void testHandleEventCancelItemReservation() throws Exception {
        final Date time = currentTime();
        ItemReservationCancelledForPortfolioEvent event =
                new ItemReservationCancelledForPortfolioEvent(portfolioIdentifier,
                        coinIdentifier,
                        transactionIdentifier,
                        DEFAULT_AMOUNT_ITEM.minus(2),
                        DEFAULT_AMOUNT_COMMISSION.minus(0.1),
                        time);

        portfolioEntry.addReserved(coinIdentifier.toString(), DEFAULT_AMOUNT_ITEM);
        portfolioEntry.confirmReserved(coinIdentifier.toString(),
                BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(1.1)));
        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                coinIdentifier.toString(),
                BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(98.9)),
                BigMoney.zero(CurrencyUnit.of(Currencies.LTC)))));
    }

    /**
     * We are going to confirm 50 of the items in the reservation. Therefore we expect the reservation to become 50
     * less than the default amount of items.
     */
    @Test
    public void testHandleEventConfirmItemReservation() {
        final Date time = currentTime();
        ItemReservationConfirmedForPortfolioEvent event =
                new ItemReservationConfirmedForPortfolioEvent(
                        portfolioIdentifier,
                        coinIdentifier,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(50)),
                        BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(1)),
                        time);

        portfolioEntry.addReserved(coinIdentifier.toString(),
                BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(100)));

        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                coinIdentifier.toString(),
                DEFAULT_AMOUNT_ITEM.minus(BigDecimal.valueOf(51)),
                BigMoney.of(CurrencyUnit.of(Currencies.LTC), BigDecimal.valueOf(49)))));
    }

    @Test
    public void testHandleItemReservedEvent() {
        final Date time = currentTime();
        ItemReservedEvent event =
                new ItemReservedEvent(
                        portfolioIdentifier,
                        coinIdentifier,
                        transactionIdentifier,
                        DEFAULT_AMOUNT_ITEM,
                        time);

        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                coinIdentifier.toString(),
                DEFAULT_AMOUNT_ITEM,
                DEFAULT_AMOUNT_ITEM)));
    }

    private PortfolioEntry createPortfolioEntry() {
        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setIdentifier(portfolioIdentifier.toString());
        portfolioEntry.setUserIdentifier(userIdentifier.toString());

        portfolioEntry.createItem(coinIdentifier.toString(), "Lite Coin");

        portfolioEntry.addItemInPossession(coinIdentifier.toString(), DEFAULT_AMOUNT_ITEM);
//        portfolioEntry.addReserved(coinIdentifier.toString(), DEFAULT_AMOUNT_COMMISSION);
        portfolioEntry.setReservedAmountOfMoney(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(1000)));
        portfolioEntry.setAmountOfMoney(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10000)));
        return portfolioEntry;
    }
}
