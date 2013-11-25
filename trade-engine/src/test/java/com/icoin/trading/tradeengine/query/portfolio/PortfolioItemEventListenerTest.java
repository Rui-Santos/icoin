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

import com.icoin.trading.api.coin.CompanyId;
import com.icoin.trading.api.orders.trades.OrderBookId;
import com.icoin.trading.api.orders.trades.PortfolioId;
import com.icoin.trading.api.orders.trades.TransactionId;
import com.icoin.trading.api.portfolio.stock.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.api.portfolio.stock.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.api.portfolio.stock.ItemsAddedToPortfolioEvent;
import com.icoin.trading.api.portfolio.stock.ItemsReservedEvent;
import com.icoin.trading.api.users.UserId;
import com.icoin.trading.query.orderbook.OrderBookEntry;
import com.icoin.trading.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.query.portfolio.repositories.PortfolioQueryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * We setup this test with a default portfolio and a default orderBook. The portfolio contains the default amount of
 * items in Reservation. This means that all available items are reserved.
 *
 * @author Jettro Coenradie
 */
public class PortfolioItemEventListenerTest {

    public static final int DEFAULT_AMOUNT_ITEMS = 100;
    private PortfolioQueryRepository portfolioQueryRepository;
    private PortfolioItemEventListener listener;

    final UserId userIdentifier = new UserId();
    final OrderBookId itemIdentifier = new OrderBookId();
    final PortfolioId portfolioIdentifier = new PortfolioId();
    final CompanyId companyIdentifier = new CompanyId();
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
        ItemsAddedToPortfolioEvent event = new ItemsAddedToPortfolioEvent(portfolioIdentifier, itemIdentifier, 100);
        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                itemIdentifier.toString(),
                1,
                2 * DEFAULT_AMOUNT_ITEMS,
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
                2 * DEFAULT_AMOUNT_ITEMS,
                0,
                0)));
    }

    /**
     * We are going to confirm 50 of the items in the reservation. Therefore we expect the reservation to become 50
     * less than the default amount of items.
     */
    @Test
    public void testHandleEventConfirmItemReservation() {
        ItemReservationConfirmedForPortfolioEvent event = new ItemReservationConfirmedForPortfolioEvent(portfolioIdentifier,
                itemIdentifier,
                transactionIdentifier,
                50);
        listener.handleEvent(event);

        Mockito.verify(portfolioQueryRepository).save(Matchers.argThat(new PortfolioEntryMatcher(
                itemIdentifier.toString(),
                1,
                DEFAULT_AMOUNT_ITEMS - 50,
                1,
                DEFAULT_AMOUNT_ITEMS - 50)));
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
                2 * DEFAULT_AMOUNT_ITEMS)));
    }

    private PortfolioEntry createPortfolioEntry() {
        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setIdentifier(portfolioIdentifier.toString());
        portfolioEntry.setUserIdentifier(userIdentifier.toString());

        portfolioEntry.addItemInPossession(createItemEntry(itemIdentifier, companyIdentifier));
        portfolioEntry.addReservedItem(createItemEntry(itemIdentifier, companyIdentifier));
        portfolioEntry.setReservedAmountOfMoney(1000);
        portfolioEntry.setAmountOfMoney(10000);
        return portfolioEntry;
    }

    private OrderBookEntry createOrderBookEntry() {
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setIdentifier(itemIdentifier.toString());
        orderBookEntry.setCompanyIdentifier(companyIdentifier.toString());
        orderBookEntry.setCompanyName("Test Company");
        return orderBookEntry;
    }

    private ItemEntry createItemEntry(OrderBookId itemIdentifier, CompanyId companyIdentifier) {
        ItemEntry itemInPossession = new ItemEntry();
        itemInPossession.setIdentifier(itemIdentifier.toString());
        itemInPossession.setCompanyIdentifier(companyIdentifier.toString());
        itemInPossession.setCompanyName("Test company");
        itemInPossession.setAmount(DEFAULT_AMOUNT_ITEMS);
        return itemInPossession;
    }
}
