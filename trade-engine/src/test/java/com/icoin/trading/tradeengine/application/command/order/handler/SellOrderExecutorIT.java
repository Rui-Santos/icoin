package com.icoin.trading.tradeengine.application.command.order.handler;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.order.ExecuteSellOrderCommand;
import com.icoin.trading.tradeengine.domain.events.order.OrderBookCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedHighestBuyPriceEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedLowestSellPriceEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.commission.Commission;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.OrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import com.icoin.trading.tradeengine.domain.model.order.TradeType;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/4/13
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SellOrderExecutorIT {
    private OrderBookId orderBookId = new OrderBookId();
    private OrderId orderId = new OrderId();
    private OrderId highestBuyOrderId = new OrderId();
    private OrderId lowestSellOrderId = new OrderId();
    private BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(100.009));
    private CoinId coinId = new CoinId("BTC");
    private BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.03));
    private BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.01));
    private LocalDate placeDate = LocalDate.now();
    private PortfolioId portfolioId = new PortfolioId();
    private TransactionId transactionId = new TransactionId();
    private Order sellOrder;

    private FixtureConfiguration fixture;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private CommissionPolicyFactory commissionPolicyFactory = mock(CommissionPolicyFactory.class);
    private OrderExecutorHelper helper = new OrderExecutorHelper();
    private SellOrderExecutor commandHandler;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(OrderBook.class);
        commandHandler = new SellOrderExecutor();
        fixture.registerAnnotatedCommandHandler(commandHandler);
        commandHandler.setOrderBookRepository(fixture.getRepository());

        OrderExecutorHelper orderExecutorHelper = new OrderExecutorHelper();
        orderExecutorHelper.setOrderRepository(orderRepository);
        commandHandler.setOrderExecutorHelper(orderExecutorHelper);

        sellOrder = new Order(OrderType.SELL);
        sellOrder.setOrderBookId(orderBookId);
        sellOrder.setTransactionId(transactionId);
        sellOrder.setPrimaryKey(orderId.toString());
        sellOrder.setPortfolioId(portfolioId);
        sellOrder.setPlaceDate(placeDate.toDate());

        helper.setOrderRepository(orderRepository);
        helper.setCommissionPolicyFactory(commissionPolicyFactory);

        commandHandler.setOrderExecutorHelper(helper);
    }

    @Test
    public void testExecuteSellOrderPriceGeLowestSell() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.04));
        sellOrder.setItemPrice(sellPrice);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents();
    }

    @Test
    public void testExecuteSellOrderPriceHigherThanBuyPrices() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.02));
        sellOrder.setItemPrice(sellPrice);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice));
    }

    @Test
    public void testExecuteSellOrderWithStrangeRepoReturns() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.01));

        when(orderRepository
                .findPendingBuyOrdersByPriceTime(
                        eq(placeDate.toDate()),
                        eq(sellPrice),
                        eq(orderBookId),
                        eq(100)))
                .thenReturn(createBuyOrders(sellPrice.minus(BigDecimal.valueOf(0.01)), sellPrice.minus(BigDecimal.valueOf(1))));

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                null,
                                BigMoney.of(sellPrice.getCurrencyUnit(), Constants.INIT_SELL_PRICE)),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                null,
                                BigMoney.zero(sellPrice.getCurrencyUnit())));


    }

    private List<Order> createBuyOrders(BigMoney... prices) {
        ArrayList<Order> list = Lists.newArrayList();

        for (int i = 0; i < prices.length; i++) {
            Order buyOrder = new Order(OrderType.BUY);
            buyOrder.setPrimaryKey(new OrderId().toString());
            buyOrder.setItemPrice(prices[i]);
            list.add(buyOrder);
        }
        return list;
    }

    @Test
    public void testExecuteSellOrderWithBuyOrders() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.0));
        BigMoney itemRemaining = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(100));
        Commission buyCommission1 = new Commission(BigMoney.of(sellPrice.getCurrencyUnit(), 1.32), "buyCommission1");
        Commission sellCommission1 = new Commission(BigMoney.of(itemRemaining.getCurrencyUnit(), 12.85), "sellCommission1");
        Commission buyCommission2 = new Commission(BigMoney.of(sellPrice.getCurrencyUnit(), 12.65), "buyCommission2");
        Commission sellCommission2 = new Commission(BigMoney.of(itemRemaining.getCurrencyUnit(), 1.51), "sellCommission2");

        sellOrder.setItemPrice(sellPrice);
        sellOrder.setItemRemaining(itemRemaining);
        sellOrder.setLeftCommission(sellCommission1.getBigMoneyCommission().plus(sellCommission2.getBigMoneyCommission()));
        when(orderRepository.findOne(eq(orderId.toString()))).thenReturn(sellOrder);

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        Order lowestSell = new Order(OrderType.SELL);
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findLowestPricePendingSellOrder(eq(orderBookId))).thenReturn(lowestSell);

        Order buyOrder1 = createBuyOrder(highestBuyPrice, itemRemaining.minus(BigDecimal.TEN), buyCommission1.getCommission());
        Order buyOrder2 = createBuyOrder(sellPrice, itemRemaining.plus(BigDecimal.valueOf(100)), buyCommission2.getCommission());
        when(orderRepository.findPendingBuyOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(buyOrder1, buyOrder2));

        BigMoney highestBuyPrice1 = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        Order highestBuy = new Order(OrderType.BUY);
        highestBuy.setItemPrice(highestBuyPrice1);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findHighestPricePendingBuyOrder(eq(orderBookId))).thenReturn(highestBuy);

        CommissionPolicy policy = mock(CommissionPolicy.class);
        when(commissionPolicyFactory.createCommissionPolicy(any(Order.class))).thenReturn(policy);
        when(policy.calculateBuyCommission(Matchers.eq(buyOrder1), eq(itemRemaining.minus(BigDecimal.TEN)), eq(highestBuyPrice)))
                .thenReturn(buyCommission1);
        when(policy.calculateBuyCommission(Matchers.eq(buyOrder2), eq(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN)), Matchers.eq(sellPrice)))
                .thenReturn(buyCommission2);
        when(policy.calculateSellCommission(Matchers.eq(sellOrder), eq(itemRemaining.minus(BigDecimal.TEN)), eq(highestBuyPrice)))
                .thenReturn(sellCommission1);
        when(policy.calculateSellCommission(Matchers.eq(sellOrder), eq(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN)), Matchers.eq(sellPrice)))
                .thenReturn(sellCommission2);


        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice),
                        new TradeExecutedEvent(
                                orderBookId,
                                coinId,
                                itemRemaining.minus(BigDecimal.TEN),
                                highestBuyPrice,
                                getExecutedMoney(itemRemaining.minus(BigDecimal.TEN), highestBuyPrice),
                                buyOrder1.getPrimaryKey(),
                                orderId.toString(),
                                buyCommission1.getBigMoneyCommission(),
                                sellCommission1.getBigMoneyCommission(),
                                buyOrder1.getTransactionId(),
                                sellOrder.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.SELL),
                        new TradeExecutedEvent(
                                orderBookId,
                                coinId,
                                BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN),
                                sellPrice,
                                getExecutedMoney(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN), sellPrice),
                                buyOrder2.getPrimaryKey(),
                                orderId.toString(),
                                buyCommission2.getBigMoneyCommission(),
                                sellCommission2.getBigMoneyCommission(),
                                buyOrder2.getTransactionId(),
                                sellOrder.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.SELL),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice1));

        //assertion
        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder1.getCompleteDate(), notNullValue());
        assertThat(buyOrder1.getCompleteDate(), equalTo(buyOrder1.getLastTradedTime()));
        assertThat(buyOrder1.getCompleteDate(), equalTo(placeDate.toDate()));
        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder1.getItemRemaining(), equalTo(BigMoney.zero(CurrencyUnit.of("BTC"))));

        assertThat(buyOrder2.getCompleteDate(), nullValue());
        assertThat(buyOrder2.getLastTradedTime(), equalTo(placeDate.toDate()));
        assertThat(buyOrder2.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(buyOrder2.getItemRemaining().isEqual(itemRemaining.plus(BigDecimal.valueOf(100)).minus(10)), is(true));

        //verify
        verify(orderRepository).findPendingBuyOrdersByPriceTime(eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));

        verify(orderRepository, times(2)).save(eq(sellOrder));
        verify(orderRepository, times(4)).save(any(Order.class));

        verify(orderRepository).findLowestPricePendingSellOrder(eq(orderBookId));
        verify(orderRepository).findHighestPricePendingBuyOrder(eq(orderBookId));

        verify(commissionPolicyFactory, times(4)).createCommissionPolicy(any(Order.class));
        verify(policy).calculateBuyCommission(Matchers.eq(buyOrder1), eq(itemRemaining.minus(BigDecimal.TEN)), eq(highestBuyPrice));
        verify(policy).calculateBuyCommission(Matchers.eq(buyOrder2), eq(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN)), Matchers.eq(sellPrice));
        verify(policy).calculateSellCommission(Matchers.eq(sellOrder), eq(itemRemaining.minus(BigDecimal.TEN)), eq(highestBuyPrice));
        verify(policy).calculateSellCommission(Matchers.eq(sellOrder), eq(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN)), Matchers.eq(sellPrice));
    }

    private Order createBuyOrder(BigMoney price, BigMoney itemRemaining, Money commission) {
        Order buyOrder = new Order(OrderType.BUY);
        buyOrder.setItemPrice(price);
        buyOrder.setItemRemaining(itemRemaining);
        buyOrder.setLeftCommission(commission.toBigMoney());
        buyOrder.setTransactionId(new TransactionId());
        buyOrder.setPrimaryKey(new OrderId().toString());

        return buyOrder;
    }

    @Test
    public void testExecuteSellOrderWithOneExactBuyOrder() throws Exception {
        BigMoney sellPrice = highestBuyPrice;
        BigMoney itemRemaining = tradeAmount;
        Commission buyCommission = new Commission(BigMoney.of(sellPrice.getCurrencyUnit(), 1.1), "buyCommission");
        Commission sellCommission = new Commission(BigMoney.of(itemRemaining.getCurrencyUnit(), 1), "sellCommission");

        sellOrder.setItemPrice(sellPrice);
        sellOrder.setItemRemaining(itemRemaining);
        sellOrder.setLeftCommission(sellCommission.getBigMoneyCommission());
        when(orderRepository.findOne(eq(orderId.toString()))).thenReturn(sellOrder);

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        Order lowestSell = new Order(OrderType.SELL);
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findLowestPricePendingSellOrder(eq(orderBookId))).thenReturn(lowestSell);

        Order buyOrder1 = createBuyOrder(highestBuyPrice, itemRemaining, buyCommission.getCommission());
        Order buyOrder2 = createBuyOrder(sellPrice, itemRemaining.plus(BigDecimal.valueOf(100)));
        when(orderRepository.findPendingBuyOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(buyOrder1, buyOrder2));

        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        Order highestBuy = new Order(OrderType.BUY);
        highestBuy.setItemPrice(highestBuyPrice);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findHighestPricePendingBuyOrder(eq(orderBookId))).thenReturn(highestBuy);

        CommissionPolicy policy = mock(CommissionPolicy.class);
        when(commissionPolicyFactory.createCommissionPolicy(any(Order.class))).thenReturn(policy);
        when(policy.calculateBuyCommission(Matchers.eq(buyOrder1), eq(itemRemaining), eq(sellPrice)))
                .thenReturn(buyCommission);
        when(policy.calculateSellCommission(Matchers.eq(sellOrder), eq(itemRemaining), eq(sellPrice)))
                .thenReturn(sellCommission);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice),
                        new TradeExecutedEvent(orderBookId,
                                coinId,
                                itemRemaining,
                                sellPrice,
                                getExecutedMoney(itemRemaining, sellPrice),
                                buyOrder1.getPrimaryKey(),
                                orderId.toString(),
                                buyCommission.getBigMoneyCommission(),
                                sellCommission.getBigMoneyCommission(),
                                buyOrder1.getTransactionId(),
                                sellOrder.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.SELL),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice));

        //assertion
        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder1.getCompleteDate(), notNullValue());
        assertThat(buyOrder1.getCompleteDate(), equalTo(buyOrder1.getLastTradedTime()));
        assertThat(buyOrder1.getCompleteDate(), equalTo(placeDate.toDate()));
        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder1.getItemRemaining().isEqual(BigMoney.zero(CurrencyUnit.of("BTC"))), is(true));

        //verify
        verify(orderRepository).findPendingBuyOrdersByPriceTime(eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));

        verify(orderRepository).save(eq(sellOrder));
        verify(orderRepository, times(2)).save(any(Order.class));

        verify(commissionPolicyFactory, times(2)).createCommissionPolicy(any(Order.class));
        verify(policy).calculateBuyCommission(Matchers.eq(buyOrder1), eq(itemRemaining), eq(sellPrice));
        verify(policy).calculateSellCommission(Matchers.eq(sellOrder), eq(itemRemaining), eq(sellPrice));
    }

    @Test
    public void testExecuteBuyOrderWithOneGreaterRemainingBuyOrder() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(99.04));
        BigMoney itemRemaining = tradeAmount.plus(1);
        Commission buyCommission = new Commission(BigMoney.of(sellPrice.getCurrencyUnit(), 1.23), "buyCommission");
        Commission sellCommission = new Commission(BigMoney.of(itemRemaining.getCurrencyUnit(), 0.9898), "sellCommission");

        sellOrder.setItemPrice(sellPrice);
        sellOrder.setItemRemaining(tradeAmount);
        sellOrder.setLeftCommission(sellCommission.getBigMoneyCommission());
        when(orderRepository.findOne(eq(orderId.toString()))).thenReturn(sellOrder);

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        Order lowestSell = new Order(OrderType.SELL);
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findLowestPricePendingSellOrder(eq(orderBookId))).thenReturn(lowestSell);

        Order buyOrder1 = createBuyOrder(highestBuyPrice, itemRemaining, buyCommission.getCommission());
        Order buyOrder2 = createBuyOrder(sellPrice, itemRemaining.plus(BigDecimal.valueOf(100)));
        when(orderRepository.findPendingBuyOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(buyOrder1, buyOrder2));

        BigMoney highestBuyPrice1 = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        Order highestBuy = new Order(OrderType.BUY);
        highestBuy.setItemPrice(highestBuyPrice1);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findHighestPricePendingBuyOrder(eq(orderBookId))).thenReturn(highestBuy);

        CommissionPolicy policy = mock(CommissionPolicy.class);
        when(commissionPolicyFactory.createCommissionPolicy(any(Order.class))).thenReturn(policy);
        when(policy.calculateBuyCommission(Matchers.eq(buyOrder1), eq(tradeAmount), eq(highestBuyPrice)))
                .thenReturn(buyCommission);
        when(policy.calculateSellCommission(Matchers.eq(sellOrder), eq(tradeAmount), eq(highestBuyPrice)))
                .thenReturn(sellCommission);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice),
                        new TradeExecutedEvent(orderBookId,
                                coinId,
                                tradeAmount,
                                highestBuyPrice,
                                getExecutedMoney(tradeAmount, highestBuyPrice),
                                buyOrder1.getPrimaryKey(),
                                orderId.toString(),
                                buyCommission.getBigMoneyCommission(),
                                sellCommission.getBigMoneyCommission(),
                                buyOrder1.getTransactionId(),
                                sellOrder.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.SELL),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice1));

        //assertion
        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder1.getCompleteDate(), nullValue());
        assertThat(buyOrder1.getLastTradedTime(), equalTo(buyOrder1.getLastTradedTime()));
        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(buyOrder1.getItemRemaining().isEqual(BigMoney.of(CurrencyUnit.of("BTC"), 1)), is(true));

        //verify
        verify(orderRepository).findPendingBuyOrdersByPriceTime(eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));

        verify(orderRepository).save(eq(sellOrder));
        verify(orderRepository, times(2)).save(any(Order.class));

        verify(commissionPolicyFactory, times(2)).createCommissionPolicy(any(Order.class));
        verify(policy).calculateBuyCommission(Matchers.eq(buyOrder1), eq(tradeAmount), eq(highestBuyPrice));
        verify(policy).calculateSellCommission(Matchers.eq(sellOrder), eq(tradeAmount), eq(highestBuyPrice));
    }

    private BigMoney getExecutedMoney(BigMoney amount, BigMoney price) {
        return getExecutedMoney(amount.getAmount(), price);
    }

    private BigMoney getExecutedMoney(BigDecimal amount, BigMoney price) {
        return Money.of(price.getCurrencyUnit(),
                amount.multiply(price.getAmount()),
                RoundingMode.HALF_EVEN).toBigMoney();
    }

    private Order createBuyOrder(BigMoney price, BigMoney itemRemaining) {
        Order buyOrder = new Order(OrderType.BUY);
        buyOrder.setItemPrice(price);
        buyOrder.setItemRemaining(itemRemaining);
        buyOrder.setTransactionId(new TransactionId());
        buyOrder.setPrimaryKey(new OrderId().toString());

        return buyOrder;
    }
}
