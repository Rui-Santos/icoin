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

package com.icoin.trading.tradeengine.application.command;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.order.CreateOrderBookCommand;
import com.icoin.trading.tradeengine.application.command.order.CreateSellOrderCommand;
import com.icoin.trading.tradeengine.application.command.order.OrderBookCommandHandler;
import com.icoin.trading.tradeengine.application.executor.TradeExecutor;
import com.icoin.trading.tradeengine.domain.events.order.BuyOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.OrderBookCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.order.SellOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.mockito.Mockito.mock;

/**
 * @author Allard Buijze
 */
public class OrderBookCommandHandlerTest {

    private FixtureConfiguration fixture;
    private SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
    private BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
    private TradeExecutor tradeExecutor = mock(TradeExecutor.class);

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(OrderBook.class);
        OrderBookCommandHandler commandHandler = new OrderBookCommandHandler();
        commandHandler.setRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(commandHandler);


        commandHandler.setBuyOrderRepository(buyOrderRepository);
        commandHandler.setSellOrderRepository(sellOrderRepository);
        commandHandler.setTradeExecutor(tradeExecutor);
    }

    @Test
    public void testSimpleTradeExecution() {
        OrderId sellOrder = new OrderId();
        PortfolioId sellingUser = new PortfolioId();
        TransactionId sellingTransaction = new TransactionId();
        OrderBookId orderBookId = new OrderBookId();
        final Date sellPlaceDate = currentTime();
        final Date buyPlaceDate = currentTime();

        CreateSellOrderCommand orderCommand = new CreateSellOrderCommand(sellOrder,
                sellingUser,
                orderBookId,
                sellingTransaction,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                sellPlaceDate);

        OrderId buyOrder = new OrderId();
        TransactionId buyTransactionId = new TransactionId();
        PortfolioId buyPortfolioId = new PortfolioId();
        final CurrencyPair currencyPair = new CurrencyPair("BTC");

        fixture.given(new OrderBookCreatedEvent(orderBookId, currencyPair),
                new BuyOrderPlacedEvent(
                        orderBookId,
                        buyOrder,
                        buyTransactionId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                        buyPortfolioId,
                        currencyPair,
                        buyPlaceDate))
                .when(orderCommand)
                .expectEvents(new SellOrderPlacedEvent(
                        orderBookId,
                        sellOrder,
                        sellingTransaction,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                        sellingUser,
                        currencyPair,
                        sellPlaceDate));
    }

    @Test
    public void testMassiveSellerTradeExecution() {
        OrderId sellOrderId = new OrderId();
        OrderId buyOrder1 = new OrderId();
        OrderId buyOrder2 = new OrderId();
        OrderId buyOrder3 = new OrderId();
        TransactionId buyTransaction1 = new TransactionId();
        TransactionId buyTransaction2 = new TransactionId();
        TransactionId buyTransaction3 = new TransactionId();

        PortfolioId sellingUser = new PortfolioId();
        TransactionId sellingTransaction = new TransactionId();

        OrderBookId orderBookId = new OrderBookId();

        final Date sellPlaceDate = currentTime();
        final Date buyPlaceDate1 = currentTime();
        final Date buyPlaceDate2 = currentTime();
        final Date buyPlaceDate3 = currentTime();

        CreateSellOrderCommand sellOrder = new CreateSellOrderCommand(sellOrderId,
                sellingUser,
                orderBookId,
                sellingTransaction,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                sellPlaceDate);
        final CurrencyPair currencyPair = new CurrencyPair("BTC");

        fixture.given(new OrderBookCreatedEvent(orderBookId, currencyPair),
                new BuyOrderPlacedEvent(orderBookId,
                        buyOrder1,
                        buyTransaction1,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                        new PortfolioId(),
                        currencyPair,
                        buyPlaceDate1),
                new BuyOrderPlacedEvent(orderBookId,
                        buyOrder2,
                        buyTransaction2,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(66)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(120)),
                        new PortfolioId(),
                        currencyPair,
                        buyPlaceDate2),
                new BuyOrderPlacedEvent(
                        orderBookId,
                        buyOrder3,
                        buyTransaction3,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(44)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(140)),
                        new PortfolioId(), currencyPair, buyPlaceDate3))
                .when(sellOrder)
                .expectEvents(new SellOrderPlacedEvent(orderBookId,
                        sellOrderId,
                        sellingTransaction,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                        sellingUser, currencyPair,
                        sellPlaceDate));
    }

    @Test
    public void testCreateOrderBook() {
        OrderBookId orderBookId = new OrderBookId();
        CreateOrderBookCommand createOrderBookCommand =
                new CreateOrderBookCommand(orderBookId, new CurrencyPair("XPM"));
        fixture.given()
                .when(createOrderBookCommand)
                .expectEvents(new OrderBookCreatedEvent(orderBookId, new CurrencyPair("XPM", "CNY")));
    }
}
