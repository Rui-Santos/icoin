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

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionConfirmedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionPartiallyExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.tradeengine.domain.TransactionType;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.transaction.repositories.TransactionQueryRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jettro Coenradie
 */
public class TransactionEventListenerTest {

    public static final TransactionId transactionIdentifier = new TransactionId();
    public static final OrderBookId orderBookIdentifier = new OrderBookId();
    public static final PortfolioId portfolioIdentifier = new PortfolioId();
    public static final CoinId coinIdentifier = new CoinId();

    public static final BigMoney DEFAULT_TOTAL_ITEMS = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100));
    public static final BigMoney DEFAULT_ITEM_PRICE = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(8.01));
    public static final BigMoney DEFAULT_TOTAL_MONEY = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(801));
    public static final BigMoney DEFAULT_BUY_COMMISSION = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(8.01));
    public static final BigMoney DEFAULT_SELL_COMMISSION = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(8.01));
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

        when(orderBookQueryRepository.findOne(orderBookIdentifier.toString()))
                .thenReturn(createOrderBookEntry());
    }

    @Test
    public void handleBuyTransactionStartedEvent() {
        final Date time = currentTime();
        BuyTransactionStartedEvent event = new BuyTransactionStartedEvent(transactionIdentifier,
                coinIdentifier,
                orderBookIdentifier,
                portfolioIdentifier,
                DEFAULT_TOTAL_ITEMS,
                DEFAULT_ITEM_PRICE,
                DEFAULT_TOTAL_MONEY,
                DEFAULT_BUY_COMMISSION,
                time);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getAmountOfItem(), equalTo(DEFAULT_TOTAL_ITEMS));
        assertThat(saved.getTotalCommission(), equalTo(DEFAULT_BUY_COMMISSION));
        assertThat(saved.getTotalMoney(), equalTo(DEFAULT_TOTAL_MONEY));
        assertThat(saved.getPrimaryKey(), equalTo(transactionIdentifier.toString()));
        assertThat(saved.getOrderBookIdentifier(), equalTo(orderBookIdentifier.toString()));
        assertThat(saved.getPortfolioIdentifier(), equalTo(portfolioIdentifier.toString()));
        assertThat(saved.getState(), equalTo(TransactionState.STARTED));
        assertThat(saved.getType(), equalTo(TransactionType.BUY));
    }

    @Test
    public void handleSellTransactionStartedEvent() {
        final Date time = currentTime();
        SellTransactionStartedEvent event = new SellTransactionStartedEvent(transactionIdentifier,
                coinIdentifier, orderBookIdentifier,
                portfolioIdentifier,
                DEFAULT_TOTAL_ITEMS,
                DEFAULT_ITEM_PRICE,
                DEFAULT_TOTAL_MONEY,
                DEFAULT_SELL_COMMISSION,
                time);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getAmountOfItem(), equalTo(DEFAULT_TOTAL_ITEMS));
        assertThat(saved.getTotalCommission(), equalTo(DEFAULT_SELL_COMMISSION));
        assertThat(saved.getTotalMoney(), equalTo(DEFAULT_TOTAL_MONEY));
        assertThat(saved.getPrimaryKey(), equalTo(transactionIdentifier.toString()));
        assertThat(saved.getOrderBookIdentifier(), equalTo(orderBookIdentifier.toString()));
        assertThat(saved.getPortfolioIdentifier(), equalTo(portfolioIdentifier.toString()));
        assertThat(saved.getCoinId(), equalTo(coinIdentifier.toString()));
        assertThat(saved.getState(), equalTo(TransactionState.STARTED));
        assertThat(saved.getType(), equalTo(TransactionType.SELL));
    }

    @Test
    public void handleSellTransactionCancelledEvent() {
        final Date time = currentTime();
        TransactionEntry transactionEntry = new TransactionEntry();
        transactionEntry.setPrimaryKey(transactionIdentifier.toString());
        transactionEntry.setAmountOfExecutedItem(BigMoney.zero(CurrencyUnit.of(Currencies.BTC)));
        transactionEntry.setState(TransactionState.STARTED);
        transactionEntry.setAmountOfItem(DEFAULT_TOTAL_ITEMS);
        transactionEntry.setOrderBookIdentifier(orderBookIdentifier.toString());
        transactionEntry.setPortfolioIdentifier(portfolioIdentifier.toString());
        transactionEntry.setType(TransactionType.SELL);

        when(transactionQueryRepository.findOne(transactionIdentifier.toString())).thenReturn(transactionEntry);
        SellTransactionCancelledEvent event =
                new SellTransactionCancelledEvent(
                        transactionIdentifier,
                        coinIdentifier,time);
        listener.handleEvent(event);
        Mockito.verify(transactionQueryRepository).save(Matchers.argThat(new TransactionEntryMatcher(
                DEFAULT_TOTAL_ITEMS,
                BigMoney.zero(CurrencyUnit.of(Currencies.BTC)),
                DEFAULT_COIN_NAME,
                TransactionState.CANCELLED,
                TransactionType.SELL

        )));
    }

    @Test
    public void testBuyTransactionCancelled() throws Exception {
        final Date time = currentTime();
        final TransactionEntry entry = new TransactionEntry();
        entry.setState(TransactionState.STARTED);
        when(transactionQueryRepository.findOne(eq(transactionIdentifier.toString())))
                .thenReturn(entry);

        final BuyTransactionCancelledEvent event = new BuyTransactionCancelledEvent(transactionIdentifier, coinIdentifier,time);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getState(), equalTo(TransactionState.CANCELLED));
    }

    @Test
    public void testSellTransactionCancelled() throws Exception {
        final Date time = currentTime();
        final TransactionEntry entry = new TransactionEntry();
        entry.setState(TransactionState.STARTED);
        when(transactionQueryRepository.findOne(eq(transactionIdentifier.toString())))
                .thenReturn(entry);

        final SellTransactionCancelledEvent event = new SellTransactionCancelledEvent(transactionIdentifier, coinIdentifier,time);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getState(), equalTo(TransactionState.CANCELLED));
    }

    @Test
    public void testBuyTransactionConfirmed() throws Exception {
        final Date confirmedDate = new Date();
        final TransactionEntry entry = new TransactionEntry();

        entry.setState(TransactionState.STARTED);
        when(transactionQueryRepository.findOne(eq(transactionIdentifier.toString())))
                .thenReturn(entry);

        final BuyTransactionConfirmedEvent event =
                new BuyTransactionConfirmedEvent(transactionIdentifier, confirmedDate);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getState(), equalTo(TransactionState.CONFIRMED));
    }

    @Test
    public void testSellTransactionConfirmed() throws Exception {
        final Date confirmedDate = new Date();
        final TransactionEntry entry = new TransactionEntry();

        entry.setState(TransactionState.STARTED);
        when(transactionQueryRepository.findOne(eq(transactionIdentifier.toString())))
                .thenReturn(entry);

        final SellTransactionConfirmedEvent event =
                new SellTransactionConfirmedEvent(transactionIdentifier, confirmedDate);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getState(), equalTo(TransactionState.CONFIRMED));
    }

    @Test
    public void testBuyTransactionExecuted() throws Exception {
        final Date time = currentTime();
        final BigMoney amount = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);
        final BigMoney price = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney money = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney commission = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);

        final TransactionEntry entry = new TransactionEntry();

        entry.setState(TransactionState.STARTED);
        when(transactionQueryRepository.findOne(eq(transactionIdentifier.toString())))
                .thenReturn(entry);

        final BuyTransactionExecutedEvent event =
                new BuyTransactionExecutedEvent(transactionIdentifier, coinIdentifier,
                        amount, price, money, commission,time);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(saved.getCommission(), equalTo(commission));
        assertThat(saved.getAmountOfExecutedItem(), equalTo(amount));
        assertThat(saved.getExecutedMoney(), equalTo(money));
    }

    @Test
    public void testSellTransactionExecuted() throws Exception {
        final Date time = currentTime();
        final BigMoney executedMoney = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 30);
        final BigMoney executedCommission = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 30);
        final BigMoney executed = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);
        final BigMoney item = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);
        final BigMoney price = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney money = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney commission = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);

        final TransactionEntry entry = new TransactionEntry();

        entry.setState(TransactionState.STARTED);
        entry.setCommission(executedCommission);
        entry.setExecutedMoney(executedMoney);
        entry.setAmountOfExecutedItem(executed);
        when(transactionQueryRepository.findOne(eq(transactionIdentifier.toString())))
                .thenReturn(entry);

        final SellTransactionExecutedEvent event =
                new SellTransactionExecutedEvent(transactionIdentifier, coinIdentifier,
                        item, price, money, commission,time);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getState(), equalTo(TransactionState.EXECUTED));
        assertThat(saved.getCommission(), equalTo(commission.plus(executedCommission)));
        assertThat(saved.getAmountOfExecutedItem(), equalTo(executed.plus(item)));
        assertThat(saved.getExecutedMoney(), equalTo(money.plus(executedMoney)));
    }

    @Test
    public void testBuyTransactionPartiallyExecuted() throws Exception {
        final Date time = currentTime();
        final BigMoney executedMoney = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 30);
        final BigMoney executedCommission = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 30);
        final BigMoney executed = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);
        final BigMoney item = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);
        final BigMoney price = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney money = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney commission = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);

        final TransactionEntry entry = new TransactionEntry();

        entry.setState(TransactionState.STARTED);
        entry.setCommission(executedCommission);
        entry.setExecutedMoney(executedMoney);
        entry.setAmountOfExecutedItem(executed);
        when(transactionQueryRepository.findOne(eq(transactionIdentifier.toString())))
                .thenReturn(entry);

        final BuyTransactionPartiallyExecutedEvent event =
                new BuyTransactionPartiallyExecutedEvent(transactionIdentifier, coinIdentifier,
                        item, item.plus(executed), price, money, commission,time);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getState(), equalTo(TransactionState.PARTIALLY_EXECUTED));
        assertThat(saved.getCommission(), equalTo(commission.plus(executedCommission)));
        assertThat(saved.getAmountOfExecutedItem(), equalTo(executed.plus(item)));
        assertThat(saved.getExecutedMoney(), equalTo(money.plus(executedMoney)));
    }

    @Test
    public void testSellTransactionPartiallyExecuted() throws Exception {
        final Date time = currentTime();
        final BigMoney executedMoney = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 30);
        final BigMoney executedCommission = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 30);
        final BigMoney executed = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);
        final BigMoney item = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);
        final BigMoney price = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney money = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney commission = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10);

        final TransactionEntry entry = new TransactionEntry();

        entry.setState(TransactionState.STARTED);
        entry.setCommission(executedCommission);
        entry.setExecutedMoney(executedMoney);
        entry.setAmountOfExecutedItem(executed);
        when(transactionQueryRepository.findOne(eq(transactionIdentifier.toString())))
                .thenReturn(entry);

        final SellTransactionPartiallyExecutedEvent event =
                new SellTransactionPartiallyExecutedEvent(transactionIdentifier, coinIdentifier,
                        item, item.plus(executed), price, money, commission,time);
        listener.handleEvent(event);

        ArgumentCaptor<TransactionEntry> captor = ArgumentCaptor.forClass(TransactionEntry.class);
        verify(transactionQueryRepository).save(captor.capture());
        final TransactionEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getState(), equalTo(TransactionState.PARTIALLY_EXECUTED));
        assertThat(saved.getCommission(), equalTo(commission.plus(executedCommission)));
        assertThat(saved.getAmountOfExecutedItem(), equalTo(executed.plus(item)));
        assertThat(saved.getExecutedMoney(), equalTo(money.plus(executedMoney)));
    }

    private OrderBookEntry createOrderBookEntry() {
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setPrimaryKey(orderBookIdentifier.toString());
        orderBookEntry.setCoinIdentifier(coinIdentifier.toString());
        orderBookEntry.setCoinName(DEFAULT_COIN_NAME);
        return orderBookEntry;
    }
}
