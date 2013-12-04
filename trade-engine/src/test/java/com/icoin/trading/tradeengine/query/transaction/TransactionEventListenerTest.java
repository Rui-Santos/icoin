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

import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionType;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.transaction.repositories.TransactionQueryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class TransactionEventListenerTest {

    public static final TransactionId transactionIdentifier = new TransactionId();
    public static final OrderBookId orderBookIdentifier = new OrderBookId();
    public static final PortfolioId portfolioIdentifier = new PortfolioId();
    public static final CoinId coinIdentifier = new CoinId();

    public static final BigDecimal DEFAULT_TOTAL_ITEMS = BigDecimal.valueOf(100);
    public static final BigDecimal DEFAULT_ITEM_PRICE = BigDecimal.TEN;
    private static final String DEFAULT_COIN_NAME = "Test Coin";

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
                BigDecimal.ZERO,
                DEFAULT_COIN_NAME,
                DEFAULT_ITEM_PRICE,
                TransactionState.STARTED,
                TransactionType.BUY

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
                BigDecimal.ZERO,
                DEFAULT_COIN_NAME,
                DEFAULT_ITEM_PRICE,
                TransactionState.STARTED,
                TransactionType.SELL

        )));
    }

    @Test
    public void handleSellTransactionCancelledEvent() {
        TransactionEntry transactionEntry = new TransactionEntry();
        transactionEntry.setPrimaryKey(transactionIdentifier.toString());
        transactionEntry.setAmountOfExecutedItems(BigDecimal.ZERO);
        transactionEntry.setPricePerItem(DEFAULT_ITEM_PRICE);
        transactionEntry.setState(TransactionState.STARTED);
        transactionEntry.setAmountOfItems(DEFAULT_TOTAL_ITEMS);
        transactionEntry.setCoinName(DEFAULT_COIN_NAME);
        transactionEntry.setOrderbookIdentifier(orderBookIdentifier.toString());
        transactionEntry.setPortfolioIdentifier(portfolioIdentifier.toString());
        transactionEntry.setType(TransactionType.SELL);

        Mockito.when(transactionQueryRepository.findOne(transactionIdentifier.toString())).thenReturn(transactionEntry);
        SellTransactionCancelledEvent event = new SellTransactionCancelledEvent(
                transactionIdentifier, DEFAULT_TOTAL_ITEMS, DEFAULT_TOTAL_ITEMS);
        listener.handleEvent(event);
        Mockito.verify(transactionQueryRepository).save(Matchers.argThat(new TransactionEntryMatcher(
                DEFAULT_TOTAL_ITEMS,
                BigDecimal.ZERO,
                DEFAULT_COIN_NAME,
                DEFAULT_ITEM_PRICE,
                TransactionState.CANCELLED,
                TransactionType.SELL

        )));
    }

    private OrderBookEntry createOrderBookEntry() {
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setPrimaryKey(orderBookIdentifier.toString());
        orderBookEntry.setCoinIdentifier(coinIdentifier.toString());
        orderBookEntry.setCoinName(DEFAULT_COIN_NAME);
        return orderBookEntry;
    }
}
