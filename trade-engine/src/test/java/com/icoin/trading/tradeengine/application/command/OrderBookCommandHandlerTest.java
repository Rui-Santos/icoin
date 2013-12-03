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

import com.icoin.trading.tradeengine.application.command.order.CreateOrderBookCommand;
import com.icoin.trading.tradeengine.application.command.order.CreateSellOrderCommand;
import com.icoin.trading.tradeengine.application.command.order.OrderBookCommandHandler;
import com.icoin.trading.tradeengine.domain.events.order.BuyOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.OrderBookCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.order.SellOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinExchangePair;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Allard Buijze
 */
public class OrderBookCommandHandlerTest {

    private FixtureConfiguration fixture;

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(OrderBook.class);
        OrderBookCommandHandler commandHandler = new OrderBookCommandHandler();
        commandHandler.setRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void testSimpleTradeExecution() {
        OrderId sellOrder = new OrderId();
        PortfolioId sellingUser = new PortfolioId();
        TransactionId sellingTransaction = new TransactionId();
        OrderBookId orderBookId = new OrderBookId();
        final Date sellPlaceDate = new Date();
        final Date buyPlaceDate = new Date();

        CreateSellOrderCommand orderCommand = new CreateSellOrderCommand(sellOrder,
                sellingUser,
                orderBookId,
                sellingTransaction,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                sellPlaceDate);

        OrderId buyOrder = new OrderId();
        CoinId coinId = new CoinId();
        TransactionId buyTransactionId = new TransactionId();
        PortfolioId buyPortfolioId = new PortfolioId();
        fixture.given(new OrderBookCreatedEvent(orderBookId, coinId, CoinExchangePair.createCoinExchangePair("BTC", "CNY")),
                new BuyOrderPlacedEvent(
                        orderBookId, buyOrder, buyTransactionId,
                        BigDecimal.valueOf(200),
                        BigDecimal.valueOf(100),
                        buyPortfolioId,
                        CoinExchangePair.createCoinExchangePair("BTC", "CNY"),
                        buyPlaceDate))
                .when(orderCommand)
                .expectEvents(new SellOrderPlacedEvent(
                        orderBookId,
                        sellOrder,
                        sellingTransaction,
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(100),
                        sellingUser,
                        CoinExchangePair.createCoinExchangePair("BTC", "CNY"),
                        sellPlaceDate),
                        new TradeExecutedEvent(orderBookId,
                                BigDecimal.valueOf(100),
                                BigDecimal.valueOf(100),
                                buyOrder.toString(),//todo change,
                                sellOrder.toString(),//todo change,
                                buyTransactionId,
                                sellingTransaction));
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
        CoinId coinId = new CoinId();

        final Date sellPlaceDate = new Date();
        final Date buyPlaceDate1 = new Date();
        final Date buyPlaceDate2 = new Date();
        final Date buyPlaceDate3 = new Date();
        CreateSellOrderCommand sellOrder = new CreateSellOrderCommand(sellOrderId,
                sellingUser,
                orderBookId,
                sellingTransaction,
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(100),
                sellPlaceDate);
        fixture.given(new OrderBookCreatedEvent(orderBookId, coinId, CoinExchangePair.createCoinExchangePair("XPM", "CNY")),
                new BuyOrderPlacedEvent(orderBookId,
                        buyOrder1,
                        buyTransaction1,
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(100),
                        new PortfolioId(),
                        CoinExchangePair.createCoinExchangePair("XPM", "CNY"),
                        buyPlaceDate1),
                new BuyOrderPlacedEvent(orderBookId,
                        buyOrder2,
                        buyTransaction2,
                        BigDecimal.valueOf(66),
                        BigDecimal.valueOf(120),
                        new PortfolioId(),
                        CoinExchangePair.createCoinExchangePair("XPM", "CNY"),
                        buyPlaceDate2),
                new BuyOrderPlacedEvent(orderBookId, buyOrder3, buyTransaction3, BigDecimal.valueOf(44), BigDecimal.valueOf(140),
                        new PortfolioId(), CoinExchangePair.createCoinExchangePair("XPM", "CNY"), buyPlaceDate3))
                .when(sellOrder)
                .expectEvents(new SellOrderPlacedEvent(orderBookId,
                        sellOrderId,
                        sellingTransaction,
                        BigDecimal.valueOf(200),
                        BigDecimal.valueOf(100),
                        sellingUser, CoinExchangePair.createCoinExchangePair("XPM", "CNY"),
                        sellPlaceDate),
                        new TradeExecutedEvent(orderBookId,
                                BigDecimal.valueOf(44),
                                BigDecimal.valueOf(120),
                                buyOrder3.toString(),//todo change,
                                sellOrderId.toString(),//todo change,
                                buyTransaction3,
                                sellingTransaction),
                        new TradeExecutedEvent(orderBookId,
                                BigDecimal.valueOf(66),
                                BigDecimal.valueOf(110),
                                buyOrder2.toString(),//todo change,
                                sellOrderId.toString(),//todo change
                                buyTransaction2,
                                sellingTransaction),
                        new TradeExecutedEvent(orderBookId,
                                BigDecimal.valueOf(90),
                                BigDecimal.valueOf(100),
                                buyOrder1.toString(),//todo change,
                                sellOrderId.toString(),//todo change,
                                buyTransaction1,
                                sellingTransaction));
    }

    @Test
    public void testCreateOrderBook() {
        OrderBookId orderBookId = new OrderBookId();
        CoinId coinId = new CoinId("XPM");
        CreateOrderBookCommand createOrderBookCommand =
                new CreateOrderBookCommand(orderBookId, coinId, CoinExchangePair.createExchangeToDefault("XPM"));
        fixture.given()
                .when(createOrderBookCommand)
                .expectEvents(new OrderBookCreatedEvent(orderBookId, coinId, CoinExchangePair.createCoinExchangePair("XPM", "CNY")));
    }
}
