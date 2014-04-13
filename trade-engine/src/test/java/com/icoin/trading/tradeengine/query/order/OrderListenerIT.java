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

import com.google.common.collect.Lists;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.OrderId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.tradeengine.events.order.BuyOrderPlacedEvent;
import com.icoin.trading.api.tradeengine.events.order.SellOrderPlacedEvent;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import org.hamcrest.Matchers;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
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
import java.util.List;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;

/**
 * @author Jettro Coenradie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
@SuppressWarnings("SpringJavaAutowiringInspection")
public class OrderListenerIT {

    private OrderListener orderListener;

    @Autowired
    private OrderQueryRepository orderRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    OrderId orderId = new OrderId();
    CoinId coinId = new CoinId();
    private PortfolioId buyPortfolioId = new PortfolioId();
    private PortfolioId sellPortfolioId = new PortfolioId();
    TransactionId transactionId = new TransactionId();
    OrderBookId orderBookId = new OrderBookId();

    @Before
    public void setUp() throws Exception {
        mongoTemplate.dropCollection(OrderEntry.class);

        orderListener = new OrderListener();
        orderListener.setOrderRepository(orderRepository);
    }

    @Test
    public void testHandleBuyOrderPlaced() throws Exception {
        final Date placeDate = new Date();
        final CurrencyPair currencyPair = new CurrencyPair("BTC", "USD");

        BuyOrderPlacedEvent event =
                new BuyOrderPlacedEvent(
                        orderBookId,
                        orderId,
                        transactionId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(9)),
                        buyPortfolioId,
                        currencyPair,
                        placeDate);

        orderListener.handleBuyOrderPlaced(event);
        Iterable<OrderEntry> all = orderRepository.findAll();
        assertThat(all, Matchers.<OrderEntry>iterableWithSize(1));
        Iterable<OrderEntry> pending =
                orderRepository.findByOrderBookIdentifierAndOrderStatus(
                        orderBookId.toString(),
                        OrderStatus.PENDING
                );

        assertThat(all, Matchers.
                <OrderEntry>containsInAnyOrder(
                        Lists.newArrayList(pending).toArray(new OrderEntry[0])));

        OrderEntry orderEntry = all.iterator().next();

        assertNotNull("The first item of the iterator for orderbooks should not be null", orderEntry);
        assertThat(orderEntry.getPrimaryKey(), equalTo(orderId.toString()));
        assertThat(orderEntry.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(orderEntry.getOrderBookIdentifier(), equalTo(orderBookId.toString()));
        assertThat(orderEntry.getTransactionIdentifier(), equalTo(transactionId.toString()));
        assertThat(orderEntry.getPlacedDate(), equalTo(placeDate));

        assertThat(orderEntry.getTradeAmount().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300))),
                is(true));
        assertThat(orderEntry.getItemRemaining().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300))),
                is(true));
        assertThat(orderEntry.getItemPrice().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(100))),
                is(true));
        assertThat(orderEntry.getPortfolioId(), equalTo(buyPortfolioId.toString()));
        assertThat(orderEntry.getCurrencyPair(), equalTo(currencyPair));
        assertThat(orderEntry.getType(), equalTo(OrderType.BUY));
        assertThat(orderEntry.getTotalCommission()
                .isEqual(BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(9))), is(true));

    }

    @Test
    public void testHandleSellOrderPlaced() throws Exception {
        final CurrencyPair currencyPair = new CurrencyPair("LTC");

        final Date placeDate = new Date();

        SellOrderPlacedEvent event =
                new SellOrderPlacedEvent(
                        orderBookId,
                        orderId,
                        transactionId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(15)),
                        sellPortfolioId,
                        currencyPair,
                        placeDate);

        orderListener.handleSellOrderPlaced(event);
        Iterable<OrderEntry> all = orderRepository.findAll();
        Iterable<OrderEntry> pending =
                orderRepository.findByOrderBookIdentifierAndOrderStatus(
                        orderBookId.toString(),
                        OrderStatus.PENDING
                );

        assertThat(all, Matchers.
                <OrderEntry>containsInAnyOrder(
                        Lists.newArrayList(pending).toArray(new OrderEntry[0])));
        OrderEntry orderEntry = all.iterator().next();

        assertNotNull("The first item of the iterator for orderbooks should not be null", orderEntry);
        assertThat(orderEntry.getPrimaryKey(), equalTo(orderId.toString()));
        assertThat(orderEntry.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(orderEntry.getOrderBookIdentifier(), equalTo(orderBookId.toString()));

        assertThat(orderEntry.getTradeAmount().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300))),
                is(true));
        assertThat(orderEntry.getItemRemaining().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300))),
                is(true));
        assertThat(orderEntry.getItemPrice().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(100))),
                is(true));
        assertThat(orderEntry.getPortfolioId(), equalTo(sellPortfolioId.toString()));
        assertThat(orderEntry.getCurrencyPair(), equalTo(currencyPair));
        assertThat(orderEntry.getType(), equalTo(OrderType.SELL));
        assertThat(orderEntry.getTotalCommission()
                .isEqual(BigMoney.of(Constants.CURRENCY_UNIT_BTC, BigDecimal.valueOf(15))), is(true));
    }

    @Test
    public void testHandleTradeExecuted() throws Exception {
        final Date tradeTime = currentTime();

        final Date sellPlaceDate = new Date();
        OrderId sellOrderId = new OrderId();
        TransactionId sellTransactionId = new TransactionId();
        CurrencyPair currencyPair = new CurrencyPair("BTC", "USD");
        SellOrderPlacedEvent sellOrderPlacedEvent =
                new SellOrderPlacedEvent(
                        orderBookId,
                        sellOrderId,
                        sellTransactionId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(400)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(3)),
                        sellPortfolioId,
                        currencyPair,
                        sellPlaceDate);

        orderListener.handleSellOrderPlaced(sellOrderPlacedEvent);

        final Date buyPlaceDate = new Date();
        OrderId buyOrderId = new OrderId();
        TransactionId buyTransactionId = new TransactionId();
        BuyOrderPlacedEvent buyOrderPlacedEvent =
                new BuyOrderPlacedEvent(orderBookId,
                        buyOrderId,
                        buyTransactionId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(150)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10)),
                        buyPortfolioId,
                        currencyPair,
                        buyPlaceDate);

        orderListener.handleBuyOrderPlaced(buyOrderPlacedEvent);

        Iterable<OrderEntry> all = orderRepository.findByOrderBookIdentifier(orderBookId.toString());
        Iterable<OrderEntry> pendingAll =
                orderRepository.findByOrderBookIdentifierAndOrderStatus(
                        orderBookId.toString(),
                        OrderStatus.PENDING);

        assertThat(all, Matchers.
                <OrderEntry>containsInAnyOrder(
                        Lists.newArrayList(pendingAll).toArray(new OrderEntry[0])));

        TradeExecutedEvent event =
                new TradeExecutedEvent(
                        orderBookId,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(125)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(37500)),
                        buyOrderId.toString(),
                        sellOrderId.toString(),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(5)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(3)),
                        buyTransactionId,
                        sellTransactionId,
                        buyPortfolioId,
                        sellPortfolioId,
                        tradeTime,
                        TradeType.BUY);
        orderListener.handleTradeExecuted(event);

        List<OrderEntry> sellOrders =
                orderRepository.findByOrderBookIdentifierAndType(
                        orderBookId.toString(),
                        OrderType.SELL
                );

        assertThat(sellOrders, hasSize(1));
        final OrderEntry sellOrderEntry = sellOrders.get(0);

        assertThat(sellOrderEntry.getOrderBookIdentifier(), equalTo(orderBookId.toString()));
        assertThat(sellOrderEntry.getPrimaryKey(), equalTo(sellOrderId.toString()));
        assertThat(sellOrderEntry.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(sellOrderEntry.getTradeAmount().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(400))),
                is(true));
        assertThat(sellOrderEntry.getItemPrice().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(100))),
                is(true));
        assertThat(sellOrderEntry.getItemRemaining().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100))),
                is(true));
        assertThat(sellOrderEntry.getType(), equalTo(OrderType.SELL));
        assertThat(sellOrderEntry.getPortfolioId(), equalTo(sellPortfolioId.toString()));
        assertThat(sellOrderEntry.getCurrencyPair(), equalTo(currencyPair));
        assertThat(sellOrderEntry.getCompleteDate(), nullValue());
        assertThat(sellOrderEntry.getLastTradedTime(), equalTo(tradeTime));
        assertThat(sellOrderEntry.getTotalCommission()
                .isEqual(BigMoney.of(Constants.CURRENCY_UNIT_BTC, BigDecimal.valueOf(3))), is(true));
        assertThat(sellOrderEntry.getExecutedCommission()
                .isEqual(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(3))),
                is(true));

        List<OrderEntry> buyOrders =
                orderRepository.findByOrderBookIdentifierAndType(
                        orderBookId.toString(),
                        OrderType.BUY
                );

        final OrderEntry buyOrderEntry = buyOrders.get(0);

        assertThat(buyOrderEntry.getOrderBookIdentifier(), equalTo(orderBookId.toString()));
        assertThat(buyOrderEntry.getPrimaryKey(), equalTo(buyOrderId.toString()));
        assertThat(buyOrderEntry.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrderEntry.getTradeAmount().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300))),
                is(true));
        assertThat(buyOrderEntry.getItemPrice().isEqual(
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(150))),
                is(true));
        assertThat(buyOrderEntry.getItemRemaining().isEqual(
                BigMoney.zero(CurrencyUnit.of(Currencies.BTC))),
                is(true));
        assertThat(buyOrderEntry.getType(), equalTo(OrderType.BUY));
        assertThat(buyOrderEntry.getPortfolioId(), equalTo(buyPortfolioId.toString()));
        assertThat(buyOrderEntry.getCurrencyPair(), equalTo(currencyPair));
        assertThat(buyOrderEntry.getCompleteDate(), equalTo(tradeTime));
        assertThat(buyOrderEntry.getLastTradedTime(), equalTo(tradeTime));
        assertThat(buyOrderEntry.getTotalCommission()
                .isEqual(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10))), is(true));
        assertThat(buyOrderEntry.getExecutedCommission()
                .isEqual(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(5))),
                is(true));

        pendingAll =
                orderRepository.findByOrderBookIdentifierAndOrderStatus(
                        orderBookId.toString(),
                        OrderStatus.PENDING
                );

        assertThat(pendingAll, contains(sellOrderEntry));
    }
}