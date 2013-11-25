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

package com.icoin.trading.tradeengine.query.transaction;

import com.icoin.trading.api.coin.CompanyId;
import com.icoin.trading.api.orders.trades.OrderBookId;
import com.icoin.trading.api.orders.trades.PortfolioId;
import com.icoin.trading.api.orders.trades.TransactionId;
import com.icoin.trading.api.orders.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.api.orders.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.api.orders.transaction.SellTransactionStartedEvent;
import com.icoin.trading.query.orderbook.OrderBookEntry;
import com.icoin.trading.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.query.transaction.repositories.TransactionQueryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static com.icoin.trading.api.orders.transaction.TransactionType.BUY;
import static com.icoin.trading.api.orders.transaction.TransactionType.SELL;
import static com.icoin.trading.query.transaction.TransactionState.CANCELLED;
import static com.icoin.trading.query.transaction.TransactionState.STARTED;


/**
 * @author Jettro Coenradie
 */
public class TransactionEventListenerTest {

    public static final TransactionId transactionIdentifier = new TransactionId();
    public static final OrderBookId orderBookIdentifier = new OrderBookId();
    public static final PortfolioId portfolioIdentifier = new PortfolioId();
    public static final CompanyId companyIdentifier = new CompanyId();

    public static final int DEFAULT_TOTAL_ITEMS = 100;
    public static final int DEFAULT_ITEM_PRICE = 10;
    private static final String DEFAULT_COMPANY_NAME = "Test Company";

    private TransactionEventListener listener;
    private TransactionQueryRepository transactionQueryRepository;

    @Before
    public void setUp() throws Exception {
        transactionQueryRepository = Mockito.mock(TransactionQueryRepository.class);
        OrderBookQueryRepository orderBookQueryRepository = Mockito.mock(OrderBookQueryRepository.class);

        listener = new TransactionEventListener();
        listener.setTransactionQueryRepository(transactionQueryRepository);
        listener.setOrderBookQueryRepository(orderBookQueryRepository);

        Mockito.when(orderBookQueryRepository.findOne(orderBookIdentifier.toString()))
                .thenReturn(createOrderBookEntry());
    }

    @Test
    public void handleBuyTransactionStartedEvent() {
        BuyTransactionStartedEvent event = new BuyTransactionStartedEvent(transactionIdentifier,
                orderBookIdentifier,
                portfolioIdentifier,
                DEFAULT_TOTAL_ITEMS,
                DEFAULT_ITEM_PRICE);
        listener.handleEvent(event);

        Mockito.verify(transactionQueryRepository).save(Matchers.argThat(new TransactionEntryMatcher(
                DEFAULT_TOTAL_ITEMS,
                0,
                DEFAULT_COMPANY_NAME,
                DEFAULT_ITEM_PRICE,
                STARTED,
                BUY

        )));
    }

    @Test
    public void handleSellTransactionStartedEvent() {
        SellTransactionStartedEvent event = new SellTransactionStartedEvent(transactionIdentifier,
                orderBookIdentifier,
                portfolioIdentifier,
                DEFAULT_TOTAL_ITEMS,
                DEFAULT_ITEM_PRICE);
        listener.handleEvent(event);

        Mockito.verify(transactionQueryRepository).save(Matchers.argThat(new TransactionEntryMatcher(
                DEFAULT_TOTAL_ITEMS,
                0,
                DEFAULT_COMPANY_NAME,
                DEFAULT_ITEM_PRICE,
                STARTED,
                SELL

        )));
    }

    @Test
    public void handleSellTransactionCancelledEvent() {
        TransactionEntry transactionEntry = new TransactionEntry();
        transactionEntry.setIdentifier(transactionIdentifier.toString());
        transactionEntry.setAmountOfExecutedItems(0);
        transactionEntry.setPricePerItem(DEFAULT_ITEM_PRICE);
        transactionEntry.setState(STARTED);
        transactionEntry.setAmountOfItems(DEFAULT_TOTAL_ITEMS);
        transactionEntry.setCompanyName(DEFAULT_COMPANY_NAME);
        transactionEntry.setOrderbookIdentifier(orderBookIdentifier.toString());
        transactionEntry.setPortfolioIdentifier(portfolioIdentifier.toString());
        transactionEntry.setType(SELL);

        Mockito.when(transactionQueryRepository.findOne(transactionIdentifier.toString())).thenReturn(transactionEntry);
        SellTransactionCancelledEvent event = new SellTransactionCancelledEvent(
                transactionIdentifier, DEFAULT_TOTAL_ITEMS, DEFAULT_TOTAL_ITEMS);
        listener.handleEvent(event);
        Mockito.verify(transactionQueryRepository).save(Matchers.argThat(new TransactionEntryMatcher(
                DEFAULT_TOTAL_ITEMS,
                0,
                DEFAULT_COMPANY_NAME,
                DEFAULT_ITEM_PRICE,
                CANCELLED,
                SELL

        )));
    }

    private OrderBookEntry createOrderBookEntry() {
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setIdentifier(orderBookIdentifier.toString());
        orderBookEntry.setCompanyIdentifier(companyIdentifier.toString());
        orderBookEntry.setCompanyName(DEFAULT_COMPANY_NAME);
        return orderBookEntry;
    }
}
