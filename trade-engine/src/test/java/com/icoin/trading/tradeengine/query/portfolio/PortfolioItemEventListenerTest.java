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

import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsReservedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.users.domain.UserId;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * We setup this test with a default portfolio and a default orderBook. The portfolio contains the default amount of
 * items in Reservation. This means that all available items are reserved.
 *
 * @author Jettro Coenradie
 */
public class PortfolioItemEventListenerTest {

    public static final BigDecimal DEFAULT_AMOUNT_ITEMS = BigDecimal.valueOf(100);
    private PortfolioQueryRepository portfolioQueryRepository;
    private PortfolioItemEventListener listener;

    final UserId userIdentifier = new UserId();
    final OrderBookId itemIdentifier = new OrderBookId();
    final PortfolioId portfolioIdentifier = new PortfolioId();
    final CoinId coinIdentifier = new CoinId();
    final TransactionId transactionIdentifier = new TransactionId();

    @Before
    public void setUp() throws Exception {
        portfolioQueryRepository = Mockito.mock(PortfolioQueryRepository.class);

        OrderBookQueryRepository orderBookQueryRepository = Mockito.mock(OrderBookQueryRepository.class);

        listener = new PortfolioItemEventListener();
        listener.setPortfolioRepository(portfolioQueryRepository);
        listener.setOrderBookQueryRepository(orderBookQueryRepository);

        OrderBookEntry orderBookEntry = createOrderBookEntry();
        Mockito.when(orderBookQueryRepository.findOne(itemIdentifier.toString())).thenReturn(orderBookEntry);

        PortfolioEntry portfolioEntry = createPortfolioEntry();
        Mockito.when(portfolioQueryRepository.findOne(portfolioIdentifier.toString())).thenReturn(portfolioEntry);
    }

    @Test
    public void testHandleEventAddItems() throws Exception {
        ItemsAddedToPortfolioEvent event =
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, itemIdentifier, BigDecimal.valueOf(100));
        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                itemIdentifier.toString(),
                1,
                DEFAULT_AMOUNT_ITEMS.multiply(BigDecimal.valueOf(2)),
                1,
                DEFAULT_AMOUNT_ITEMS)));
    }

    @Test
    public void testHandleEventCancelItemReservation() throws Exception {
        ItemReservationCancelledForPortfolioEvent event =
                new ItemReservationCancelledForPortfolioEvent(portfolioIdentifier,
                        itemIdentifier,
                        transactionIdentifier,
                        DEFAULT_AMOUNT_ITEMS);
        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                itemIdentifier.toString(),
                1,
                DEFAULT_AMOUNT_ITEMS.multiply(BigDecimal.valueOf(2)),
                0,
                BigDecimal.ZERO)));
    }

    /**
     * We are going to confirm 50 of the items in the reservation. Therefore we expect the reservation to become 50
     * less than the default amount of items.
     */
    @Test
    public void testHandleEventConfirmItemReservation() {
        ItemReservationConfirmedForPortfolioEvent event =
                new ItemReservationConfirmedForPortfolioEvent(
                        portfolioIdentifier,
                        itemIdentifier,
                        transactionIdentifier,
                        BigDecimal.valueOf(50));
        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                itemIdentifier.toString(),
                1,
                DEFAULT_AMOUNT_ITEMS.subtract(BigDecimal.valueOf(50)),
                1,
                DEFAULT_AMOUNT_ITEMS.subtract(BigDecimal.valueOf(50)))));
    }

    @Test
    public void testHandleItemReservedEvent() {
        ItemsReservedEvent event = new ItemsReservedEvent(portfolioIdentifier, itemIdentifier, transactionIdentifier, DEFAULT_AMOUNT_ITEMS);
        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                itemIdentifier.toString(),
                1,
                DEFAULT_AMOUNT_ITEMS,
                1,
                DEFAULT_AMOUNT_ITEMS.multiply(BigDecimal.valueOf(2)))));
    }

    private PortfolioEntry createPortfolioEntry() {
        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setIdentifier(portfolioIdentifier.toString());
        portfolioEntry.setUserIdentifier(userIdentifier.toString());

        portfolioEntry.addItemInPossession(createItemEntry(itemIdentifier, coinIdentifier));
        portfolioEntry.addReservedItem(createItemEntry(itemIdentifier, coinIdentifier));
        portfolioEntry.setReservedAmountOfMoney(BigDecimal.valueOf(1000));
        portfolioEntry.setAmountOfMoney(BigDecimal.valueOf(10000));
        return portfolioEntry;
    }

    private OrderBookEntry createOrderBookEntry() {
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setPrimaryKey(itemIdentifier.toString());
        orderBookEntry.setCoinIdentifier(coinIdentifier.toString());
        orderBookEntry.setCoinName("Test Coin");
        return orderBookEntry;
    }

    private ItemEntry createItemEntry(OrderBookId itemIdentifier, CoinId coinIdentifier) {
        ItemEntry itemInPossession = new ItemEntry();
        itemInPossession.setPrimaryKey(itemIdentifier.toString());
        itemInPossession.setCoinIdentifier(coinIdentifier.toString());
        itemInPossession.setCoinName("Test coin");
        itemInPossession.setAmount(DEFAULT_AMOUNT_ITEMS);
        return itemInPossession;
    }
}
