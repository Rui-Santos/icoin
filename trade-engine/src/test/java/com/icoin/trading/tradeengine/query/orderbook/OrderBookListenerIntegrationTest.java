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

package com.icoin.trading.tradeengine.query.orderbook;

import com.icoin.trading.tradeengine.domain.events.coin.CoinCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.coin.OrderBookAddedToCoinEvent;
import com.icoin.trading.tradeengine.domain.events.order.BuyOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.SellOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.CoinListener;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Jettro Coenradie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/spring/persistence-infrastructure-context.xml"})
@SuppressWarnings("SpringJavaAutowiringInspection")
public class OrderBookListenerIntegrationTest {

    private OrderBookListener orderBookListener;

    @Autowired
    private OrderBookQueryRepository orderBookRepository;

    @Autowired
    private TradeExecutedQueryRepository tradeExecutedRepository;

    @Autowired
    private CoinQueryRepository coinRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    OrderId orderId = new OrderId();
    PortfolioId portfolioId = new PortfolioId();
    TransactionId transactionId = new TransactionId();
    OrderBookId orderBookId = new OrderBookId();
    CoinId companyId = new CoinId();

    @Before
    public void setUp() throws Exception {
        mongoTemplate.dropCollection(OrderBookEntry.class);
        mongoTemplate.dropCollection(CoinEntry.class);
        mongoTemplate.dropCollection(TradeExecutedEntry.class);

        CoinListener companyListener = new CoinListener();
        companyListener.setCoinRepository(coinRepository);
        companyListener.handleCoinCreatedEvent(
                new CoinCreatedEvent(companyId, "Test Coin", BigDecimal.valueOf(100), BigDecimal.valueOf(100)));

        orderBookListener = new OrderBookListener();
        orderBookListener.setCoinRepository(coinRepository);
        orderBookListener.setOrderBookRepository(orderBookRepository);
        orderBookListener.setTradeExecutedRepository(tradeExecutedRepository);
    }

    @Test
    public void testHandleOrderBookCreatedEvent() throws Exception {
        OrderBookAddedToCoinEvent event = new OrderBookAddedToCoinEvent(companyId, orderBookId);

        orderBookListener.handleOrderBookAddedToCoinEvent(event);
        Iterable<OrderBookEntry> all = orderBookRepository.findAll();
        OrderBookEntry orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
    }

    @Test
    public void testHandleBuyOrderPlaced() throws Exception {
        CoinEntry company = createCoin();
        OrderBookEntry orderBook = createOrderBook(company);

        final Date placeDate = new Date();

        BuyOrderPlacedEvent event =
                new BuyOrderPlacedEvent(
                        orderBookId,
                        orderId,
                        transactionId,
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(100),
                        portfolioId,
                        CurrencyPair.createCurrencyPair("BTC", "USD"),
                        placeDate);

        orderBookListener.handleBuyOrderPlaced(event);
        Iterable<OrderBookEntry> all = orderBookRepository.findAll();
        OrderBookEntry orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
        assertEquals(1, orderBookEntry.buyOrders().size());
//        assertEquals(300, orderBookEntry.buyOrders().get(0).getTradeAmount());
        closeTo(300.00, orderBookEntry.buyOrders().get(0).getTradeAmount().doubleValue());
    }

    @Test
    public void testHandleSellOrderPlaced() throws Exception {
        CoinEntry company = createCoin();
        OrderBookEntry orderBook = createOrderBook(company);

        final Date placeDate = new Date();

        OrderBookId orderBookId = new OrderBookId(orderBook.getIdentifier());
        SellOrderPlacedEvent event =
                new SellOrderPlacedEvent(
                        orderBookId,
                        orderId,
                        transactionId,
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(100),
                        portfolioId,
                        CurrencyPair.createCurrencyPair("BTC", "USD"),
                        placeDate);

        orderBookListener.handleSellOrderPlaced(event);
        Iterable<OrderBookEntry> all = orderBookRepository.findAll();
        OrderBookEntry orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
        assertEquals(1, orderBookEntry.sellOrders().size());
        assertEquals(300, orderBookEntry.sellOrders().get(0).getTradeAmount());
    }

    @Test
    public void testHandleTradeExecuted() throws Exception {
        CoinEntry company = createCoin();
        OrderBookEntry orderBook = createOrderBook(company);

        final Date sellPlaceDate = new Date();
        OrderId sellOrderId = new OrderId();
        TransactionId sellTransactionId = new TransactionId();
        SellOrderPlacedEvent sellOrderPlacedEvent =
                new SellOrderPlacedEvent(
                        orderBookId,
                        sellOrderId,
                        sellTransactionId,
                        BigDecimal.valueOf(400),
                        BigDecimal.valueOf(100),
                        portfolioId,
                        CurrencyPair.createCurrencyPair("BTC", "USD"),
                        sellPlaceDate);

        orderBookListener.handleSellOrderPlaced(sellOrderPlacedEvent);

        final Date buyPlaceDate = new Date();
        OrderId buyOrderId = new OrderId();
        TransactionId buyTransactionId = new TransactionId();
        BuyOrderPlacedEvent buyOrderPlacedEvent = new BuyOrderPlacedEvent(orderBookId
                , buyOrderId,
                buyTransactionId,
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(150),
                portfolioId,
                CurrencyPair.createCurrencyPair("BTC", "USD"),
                buyPlaceDate);

        orderBookListener.handleBuyOrderPlaced(buyOrderPlacedEvent);

        Iterable<OrderBookEntry> all = orderBookRepository.findAll();
        OrderBookEntry orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
        assertEquals(1, orderBookEntry.sellOrders().size());
        assertEquals(1, orderBookEntry.buyOrders().size());


        TradeExecutedEvent event = new TradeExecutedEvent(orderBookId,
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(125),
                buyOrderId,
                sellOrderId,
                buyTransactionId,
                sellTransactionId);
        orderBookListener.handleTradeExecuted(event);

        Iterable<TradeExecutedEntry> tradeExecutedEntries = tradeExecutedRepository.findAll();
        assertTrue(tradeExecutedEntries.iterator().hasNext());
        TradeExecutedEntry tradeExecutedEntry = tradeExecutedEntries.iterator().next();
        assertEquals("Test Coin", tradeExecutedEntry.getCoinName());
        assertEquals(300, tradeExecutedEntry.getTradeAmount());
        assertEquals(125, tradeExecutedEntry.getTradePrice());

        all = orderBookRepository.findAll();
        orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
        assertEquals(1, orderBookEntry.sellOrders().size());
        assertEquals(0, orderBookEntry.buyOrders().size());
    }


    private OrderBookEntry createOrderBook(CoinEntry company) {
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setIdentifier(orderBookId.toString());
        orderBookEntry.setCoinIdentifier(company.getIdentifier());
        orderBookEntry.setCoinName(company.getName());
        orderBookRepository.save(orderBookEntry);
        return orderBookEntry;
    }

    private CoinEntry createCoin() {
        CoinId companyId = new CoinId();
        CoinEntry companyEntry = new CoinEntry();
        companyEntry.setIdentifier(companyId.toString());
        companyEntry.setName("Test Coin");
        companyEntry.setCoinInitialAmount(BigDecimal.valueOf(100000));
        companyEntry.setTradeStarted(true);
        companyEntry.setCoinInitialPrice(BigDecimal.valueOf(1000));
        coinRepository.save(companyEntry);
        return companyEntry;
    }
}
