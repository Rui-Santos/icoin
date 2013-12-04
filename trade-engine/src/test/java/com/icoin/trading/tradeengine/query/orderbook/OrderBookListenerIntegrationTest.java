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
import com.icoin.trading.tradeengine.domain.model.coin.CoinExchangePair;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.CoinListener;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderBookListener;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
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
    CoinId coinId = new CoinId();

    @Before
    public void setUp() throws Exception {
        mongoTemplate.dropCollection(OrderBookEntry.class);
        mongoTemplate.dropCollection(CoinEntry.class);
        mongoTemplate.dropCollection(TradeExecutedEntry.class);

        CoinListener coinListener = new CoinListener();
        coinListener.setCoinRepository(coinRepository);
        coinListener.handleCoinCreatedEvent(
                new CoinCreatedEvent(coinId, "Test Coin", BigDecimal.valueOf(100), BigDecimal.valueOf(100)));

        orderBookListener = new OrderBookListener();
        orderBookListener.setCoinRepository(coinRepository);
        orderBookListener.setOrderBookRepository(orderBookRepository);
        orderBookListener.setTradeExecutedRepository(tradeExecutedRepository);
    }

    @Test
    public void testHandleOrderBookCreatedEvent() throws Exception {
        OrderBookAddedToCoinEvent event = new OrderBookAddedToCoinEvent(coinId, orderBookId);

        orderBookListener.handleOrderBookAddedToCoinEvent(event);
        Iterable<OrderBookEntry> all = orderBookRepository.findAll();
        OrderBookEntry orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
    }

    @Test
    public void testHandleBuyOrderPlaced() throws Exception {
        CoinEntry coin = createCoin();
        OrderBookEntry orderBook = createOrderBook(coin);

        final Date placeDate = new Date();

        BuyOrderPlacedEvent event =
                new BuyOrderPlacedEvent(
                        orderBookId,
                        orderId,
                        transactionId,
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(100),
                        portfolioId,
                        CoinExchangePair.createCoinExchangePair("BTC", "USD"),
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
        CoinEntry coin = createCoin();
        OrderBookEntry orderBook = createOrderBook(coin);

        final Date placeDate = new Date();

        OrderBookId orderBookId = new OrderBookId(orderBook.getPrimaryKey());
        SellOrderPlacedEvent event =
                new SellOrderPlacedEvent(
                        orderBookId,
                        orderId,
                        transactionId,
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(100),
                        portfolioId,
                        CoinExchangePair.createCoinExchangePair("BTC", "USD"),
                        placeDate);

        orderBookListener.handleSellOrderPlaced(event);
        Iterable<OrderBookEntry> all = orderBookRepository.findAll();
        OrderBookEntry orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
        assertEquals(1, orderBookEntry.sellOrders().size());
        closeTo(300.00, orderBookEntry.sellOrders().get(0).getTradeAmount().doubleValue());
    }

    @Test
    public void testHandleTradeExecuted() throws Exception {
        CoinEntry coin = createCoin();
        OrderBookEntry orderBook = createOrderBook(coin);

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
                        CoinExchangePair.createCoinExchangePair("BTC", "USD"),
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
                CoinExchangePair.createCoinExchangePair("BTC", "USD"),
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
                buyOrderId.toString(),//todo change,
                sellOrderId.toString(),//todo change,
                buyTransactionId,
                sellTransactionId);
        orderBookListener.handleTradeExecuted(event);

        Iterable<TradeExecutedEntry> tradeExecutedEntries = tradeExecutedRepository.findAll();
        assertTrue(tradeExecutedEntries.iterator().hasNext());
        TradeExecutedEntry tradeExecutedEntry = tradeExecutedEntries.iterator().next();
        assertEquals("Test Coin", tradeExecutedEntry.getCoinName());
        closeTo(300.00, tradeExecutedEntry.getTradeAmount().doubleValue());
        closeTo(125, tradeExecutedEntry.getTradePrice().doubleValue());

        all = orderBookRepository.findAll();
        orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
        assertEquals(1, orderBookEntry.sellOrders().size());
        assertEquals(0, orderBookEntry.buyOrders().size());
    }


    private OrderBookEntry createOrderBook(CoinEntry coin) {
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setPrimaryKey(orderBookId.toString());
        orderBookEntry.setCoinIdentifier(coin.getPrimaryKey());
        orderBookEntry.setCoinName(coin.getName());
        orderBookRepository.save(orderBookEntry);
        return orderBookEntry;
    }

    private CoinEntry createCoin() {
        CoinId coinId = new CoinId();
        CoinEntry coinEntry = new CoinEntry();
        coinEntry.setPrimaryKey(coinId.toString());
        coinEntry.setName("Test Coin");
        coinEntry.setCoinAmount(BigDecimal.valueOf(100000));
        coinEntry.setTradeStarted(true);
        coinEntry.setCoinPrice(BigDecimal.valueOf(1000));
        coinRepository.save(coinEntry);
        return coinEntry;
    }
}
