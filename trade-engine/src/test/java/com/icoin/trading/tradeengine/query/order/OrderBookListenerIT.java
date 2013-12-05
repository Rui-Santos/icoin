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

package com.icoin.trading.tradeengine.query.order;

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

import static com.homhon.mongo.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
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
public class OrderBookListenerIT {

    private OrderBookListener orderBookListener;

    @Autowired
    private OrderBookQueryRepository orderBookRepository;

    @Autowired
    private TradeExecutedQueryRepository tradeExecutedRepository;

    @Autowired
    private CoinQueryRepository coinRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

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
    public void testHandleTradeExecuted() throws Exception {
        CoinEntry coin = createCoin();
        OrderId sellOrderId = new OrderId();
        OrderId buyOrderId = new OrderId();
        TransactionId sellTransactionId = new TransactionId();
        TransactionId buyTransactionId = new TransactionId();
        final Date tradeTime = currentTime();

        TradeExecutedEvent event = new TradeExecutedEvent(orderBookId,
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(125),
                buyOrderId.toString(),//todo change,
                sellOrderId.toString(),//todo change,
                buyTransactionId,
                sellTransactionId,
                tradeTime);

        //execute
        orderBookListener.handleTradeExecuted(event);

        //verify
        Iterable<OrderBookEntry> all = orderBookRepository.findAll();
        OrderBookEntry orderBookEntry = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookEntry);
        assertEquals("Test Coin", orderBookEntry.getCoinName());
        assertThat(orderBookEntry.getTradedPrice(),equalTo(BigDecimal.valueOf(125)));
        assertThat(orderBookEntry.getBuyTransactionId(),equalTo(buyTransactionId.toString()));
        assertThat(orderBookEntry.getSellTransactionId(),equalTo(sellTransactionId.toString()));
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
