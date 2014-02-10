package com.icoin.trading.tradeengine.application.command.order.handler;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.order.ExecuteBuyOrderCommand;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
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
//@Ignore
public class BuyOrderExecutorIT {
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
    private Order buyOrder;

    private FixtureConfiguration fixture;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private CommissionPolicyFactory commissionPolicyFactory = mock(CommissionPolicyFactory.class);
    private OrderExecutorHelper helper = new OrderExecutorHelper();
    private BuyOrderExecutor commandHandler;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(OrderBook.class);
        commandHandler = new BuyOrderExecutor();
        fixture.registerAnnotatedCommandHandler(commandHandler);
        commandHandler.setOrderBookRepository(fixture.getRepository());

        OrderExecutorHelper orderExecutorHelper = new OrderExecutorHelper();
        orderExecutorHelper.setOrderRepository(orderRepository);
        commandHandler.setOrderExecutorHelper(orderExecutorHelper);

        buyOrder = new Order(OrderType.BUY);
        buyOrder.setOrderBookId(orderBookId);
        buyOrder.setTransactionId(transactionId);
        buyOrder.setPrimaryKey(orderId.toString());
        buyOrder.setPortfolioId(portfolioId);
        buyOrder.setPlaceDate(placeDate.toDate());

        helper.setOrderRepository(orderRepository);
        helper.setCommissionPolicyFactory(commissionPolicyFactory);

        commandHandler.setOrderExecutorHelper(helper);
    }

    @Test
    public void testExecuteBuyOrderPriceLeHighestBuy() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.0));
        buyOrder.setItemPrice(buyPrice);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
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
    public void testExecuteBuyOrderPriceLowerThanSellPrices() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.02));
        buyOrder.setItemPrice(buyPrice);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
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
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice));
    }

    @Test
    public void testExecuteBuyOrderWithStrangeRepoReturns() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.04));
        buyOrder.setItemPrice(buyPrice);

        when(orderRepository.findPendingSellOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100)))
                .thenReturn(createSellOrders(buyPrice.plus(BigDecimal.valueOf(0.01)), buyPrice.plus(BigDecimal.valueOf(1))));

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
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
                .expectEvents(
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                orderId.toString(),
                                buyPrice),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                null,
                                BigMoney.of(buyPrice.getCurrencyUnit(), Constants.INIT_SELL_PRICE)),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                null,
                                BigMoney.zero(buyPrice.getCurrencyUnit())));


    }

    private List<Order> createSellOrders(BigMoney... prices) {
        ArrayList<Order> list = Lists.newArrayList();

        for (int i = 0; i < prices.length; i++) {
            Order sellOrder = new Order(OrderType.SELL);
            sellOrder.setPrimaryKey(new OrderId().toString());
            sellOrder.setItemPrice(prices[i]);
            list.add(sellOrder);
        }
        return list;
    }

    @Test
    public void testExecuteBuyOrderWithSellOrders() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.04));
        BigMoney itemRemaining = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(100));
        buyOrder.setItemPrice(buyPrice);
        buyOrder.setItemRemaining(itemRemaining);
        buyOrder.setLeftCommission(BigMoney.of(lowestSellPrice.getCurrencyUnit(), 16));
        when(orderRepository.findOne(eq(orderId.toString()))).thenReturn(buyOrder);


        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        Order highestBuy = new Order(OrderType.BUY);
        highestBuy.setItemPrice(highestBuyPrice);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findHighestPricePendingBuyOrder(eq(orderBookId))).thenReturn(highestBuy);

        Order sellOrder1 = createSellOrder(highestBuyPrice, itemRemaining.minus(BigDecimal.TEN), BigMoney.of(tradeAmount.getCurrencyUnit(), 1.85));
        Order sellOrder2 = createSellOrder(buyPrice, itemRemaining.plus(BigDecimal.valueOf(100)), BigMoney.of(tradeAmount.getCurrencyUnit(), 12.5));
        when(orderRepository.findPendingSellOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(sellOrder1, sellOrder2));

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        Order lowestSell = new Order(OrderType.SELL);
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findLowestPricePendingSellOrder(eq(orderBookId))).thenReturn(lowestSell);

        Commission buyCommission1 = new Commission(BigMoney.of(buyPrice.getCurrencyUnit(), 12.89), "buyCommission1");
        Commission sellCommission1 = new Commission(BigMoney.of(itemRemaining.getCurrencyUnit(), 1.85), "sellCommission1");
        Commission buyCommission2 = new Commission(BigMoney.of(buyPrice.getCurrencyUnit(), 2.65), "buyCommission2");
        Commission sellCommission2 = new Commission(BigMoney.of(itemRemaining.getCurrencyUnit(), 1.5), "sellCommission2");

        CommissionPolicy policy = mock(CommissionPolicy.class);
        when(commissionPolicyFactory.createCommissionPolicy(any(Order.class))).thenReturn(policy);
        when(policy.calculateBuyCommission(eq(buyOrder), eq(itemRemaining.minus(BigDecimal.TEN)), eq(highestBuyPrice)))
                .thenReturn(buyCommission1);
        when(policy.calculateBuyCommission(eq(buyOrder), eq(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN)), eq(buyPrice)))
                .thenReturn(buyCommission2);
        when(policy.calculateSellCommission(eq(sellOrder1), eq(itemRemaining.minus(BigDecimal.TEN)), eq(highestBuyPrice)))
                .thenReturn(sellCommission1);
        when(policy.calculateSellCommission(eq(sellOrder2), eq(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN)), eq(buyPrice)))
                .thenReturn(sellCommission2);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
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
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice),
                        new TradeExecutedEvent(orderBookId,
                                coinId,
                                itemRemaining.minus(BigDecimal.TEN),
                                highestBuyPrice,
                                getExecutedMoney(itemRemaining.minus(BigDecimal.TEN), highestBuyPrice),
                                orderId.toString(),
                                sellOrder1.getPrimaryKey(),
                                buyCommission1.getBigMoneyCommission(),
                                sellCommission1.getBigMoneyCommission(),
                                buyOrder.getTransactionId(),
                                sellOrder1.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.BUY),
                        new TradeExecutedEvent(orderBookId,
                                coinId,
                                BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN),
                                buyPrice,
                                getExecutedMoney(BigDecimal.TEN, buyPrice),
                                orderId.toString(),
                                sellOrder2.getPrimaryKey(),
                                buyCommission2.getBigMoneyCommission(),
                                sellCommission2.getBigMoneyCommission(),
                                buyOrder.getTransactionId(),
                                sellOrder2.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.BUY),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice));

        //assertion
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder.getLeftCommission(), equalTo(BigMoney.of(buyPrice.getCurrencyUnit(), 0.46)));

        assertThat(sellOrder1.getCompleteDate(), notNullValue());
        assertThat(sellOrder1.getCompleteDate(), equalTo(sellOrder1.getLastTradedTime()));
        assertThat(sellOrder1.getCompleteDate(), equalTo(placeDate.toDate()));
        assertThat(sellOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder1.getItemRemaining(), equalTo(BigMoney.zero(CurrencyUnit.of("BTC"))));
        assertThat(sellOrder1.getLeftCommission().isEqual(BigMoney.zero(CurrencyUnit.of("BTC"))), is(true));

        assertThat(sellOrder2.getCompleteDate(), nullValue());
        assertThat(sellOrder2.getLastTradedTime(), equalTo(placeDate.toDate()));
        assertThat(sellOrder2.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(sellOrder2.getItemRemaining().isEqual(itemRemaining.plus(BigDecimal.valueOf(100)).minus(10)), is(true));
        assertThat(sellOrder2.getLeftCommission().isEqual(BigMoney.of(itemRemaining.getCurrencyUnit(), 11)), is(true));

        //verify
        verify(orderRepository).findPendingSellOrdersByPriceTime(eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100));

        verify(orderRepository, times(2)).save(eq(buyOrder));
        verify(orderRepository).save(eq(sellOrder1));
        verify(orderRepository).save(eq(sellOrder2));

        verify(orderRepository).findLowestPricePendingSellOrder(eq(orderBookId));
        verify(orderRepository).findHighestPricePendingBuyOrder(eq(orderBookId));

        verify(commissionPolicyFactory, times(4)).createCommissionPolicy(any(Order.class));
        verify(policy).calculateBuyCommission(eq(buyOrder), eq(itemRemaining.minus(BigDecimal.TEN)), eq(highestBuyPrice));
        verify(policy).calculateBuyCommission(eq(buyOrder), eq(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN)), eq(buyPrice));
        verify(policy).calculateSellCommission(eq(sellOrder1), eq(itemRemaining.minus(BigDecimal.TEN)), eq(highestBuyPrice));
        verify(policy).calculateSellCommission(eq(sellOrder2), eq(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN)), eq(buyPrice));
    }

    @Test
    public void testExecuteBuyOrderWithOneExactSellOrder() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.05));
        BigMoney itemRemaining = tradeAmount;
        buyOrder.setItemPrice(buyPrice);
        buyOrder.setItemRemaining(itemRemaining);
        buyOrder.setLeftCommission(BigMoney.of(lowestSellPrice.getCurrencyUnit(), 10.25));
        when(orderRepository.findOne(eq(orderId.toString()))).thenReturn(buyOrder);

        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        Order highestBuy = new Order(OrderType.BUY);
        highestBuy.setItemPrice(highestBuyPrice);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findHighestPricePendingBuyOrder(eq(orderBookId))).thenReturn(highestBuy);

        Order sellOrder1 = createSellOrder(highestBuyPrice, itemRemaining, BigMoney.of(tradeAmount.getCurrencyUnit(), 1.98));
        Order sellOrder2 = createSellOrder(buyPrice, itemRemaining.plus(BigDecimal.valueOf(100)), BigMoney.of(tradeAmount.getCurrencyUnit(), 12.5));
        when(orderRepository.findPendingSellOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(sellOrder1, sellOrder2));

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        Order lowestSell = new Order(OrderType.SELL);
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(orderRepository.findLowestPricePendingSellOrder(eq(orderBookId))).thenReturn(lowestSell);

        Commission buyCommission = new Commission(BigMoney.of(buyPrice.getCurrencyUnit(), 1), "buyCommission");
        Commission sellCommission = new Commission(BigMoney.of(itemRemaining.getCurrencyUnit(), 1.98), "sellCommission");

        CommissionPolicy policy = mock(CommissionPolicy.class);
        when(commissionPolicyFactory.createCommissionPolicy(any(Order.class))).thenReturn(policy);
        when(policy.calculateBuyCommission(eq(buyOrder), eq(itemRemaining), eq(highestBuyPrice)))
                .thenReturn(buyCommission);
        when(policy.calculateSellCommission(eq(sellOrder1), eq(itemRemaining), eq(highestBuyPrice)))
                .thenReturn(sellCommission);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
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
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice),
                        new TradeExecutedEvent(orderBookId,
                                coinId,
                                itemRemaining,
                                highestBuyPrice,
                                getExecutedMoney(itemRemaining, highestBuyPrice),
                                orderId.toString(),
                                sellOrder1.getPrimaryKey(),
                                buyCommission.getBigMoneyCommission(),
                                sellCommission.getBigMoneyCommission(),
                                buyOrder.getTransactionId(),
                                sellOrder1.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.BUY),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice));

        //assertion
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder.getLeftCommission(), equalTo(BigMoney.of(buyPrice.getCurrencyUnit(), 9.25)));

        assertThat(sellOrder1.getCompleteDate(), notNullValue());
        assertThat(sellOrder1.getCompleteDate(), equalTo(sellOrder1.getLastTradedTime()));
        assertThat(sellOrder1.getCompleteDate(), equalTo(placeDate.toDate()));
        assertThat(sellOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder1.getItemRemaining().isEqual(BigMoney.zero(CurrencyUnit.of("BTC"))), is(true));
        assertThat(sellOrder1.getLeftCommission().isEqual(BigMoney.zero(CurrencyUnit.of("BTC"))), is(true));

        //verify
        verify(orderRepository).findPendingSellOrdersByPriceTime(eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100));

        verify(orderRepository).save(eq(buyOrder));
        verify(orderRepository, times(2)).save(any(Order.class));

        verify(orderRepository).findLowestPricePendingSellOrder(eq(orderBookId));
        verify(orderRepository).findHighestPricePendingBuyOrder(eq(orderBookId));

        verify(commissionPolicyFactory, times(2)).createCommissionPolicy(any(Order.class));
        verify(policy).calculateBuyCommission(eq(buyOrder), eq(itemRemaining), eq(highestBuyPrice));
        verify(policy).calculateSellCommission(eq(sellOrder1), eq(itemRemaining), eq(highestBuyPrice));
    }

    @Test
    public void testExecuteBuyOrderWithOneGreaterRemainingSellOrder() throws Exception {
        BigMoney buyPrice = lowestSellPrice;
        BigMoney itemRemaining = tradeAmount.plus(1);
        buyOrder.setItemPrice(buyPrice);
        buyOrder.setItemRemaining(itemRemaining);
        buyOrder.setLeftCommission(BigMoney.of(lowestSellPrice.getCurrencyUnit(), 1.23));
        when(orderRepository.findOne(eq(orderId.toString()))).thenReturn(buyOrder);

        Order sellOrder1 = createSellOrder(buyPrice, itemRemaining.plus(1), BigMoney.of(tradeAmount.getCurrencyUnit(), 1.25));
        Order sellOrder2 = createSellOrder(buyPrice, itemRemaining.plus(BigDecimal.valueOf(100)), BigMoney.of(tradeAmount.getCurrencyUnit(), 1.25));
        when(orderRepository.findPendingSellOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100)))
                .thenReturn(Arrays.asList(sellOrder1, sellOrder2));

        Commission buyCommission = new Commission(BigMoney.of(buyPrice.getCurrencyUnit(), 1.23), "buyCommission");
        Commission sellCommission = new Commission(BigMoney.of(itemRemaining.getCurrencyUnit(), 0.9898), "sellCommission");

        CommissionPolicy policy = mock(CommissionPolicy.class);
        when(commissionPolicyFactory.createCommissionPolicy(any(Order.class))).thenReturn(policy);
        when(policy.calculateBuyCommission(eq(buyOrder), eq(itemRemaining), eq(lowestSellPrice)))
                .thenReturn(buyCommission);
        when(policy.calculateSellCommission(eq(sellOrder1), eq(itemRemaining), eq(lowestSellPrice)))
                .thenReturn(sellCommission);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
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
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice),
                        new TradeExecutedEvent(orderBookId,
                                coinId,
                                itemRemaining,
                                lowestSellPrice,
                                getExecutedMoney(itemRemaining, lowestSellPrice),
                                orderId.toString(),
                                sellOrder1.getPrimaryKey(),
                                buyCommission.getBigMoneyCommission(),
                                sellCommission.getBigMoneyCommission(),
                                buyOrder.getTransactionId(),
                                sellOrder1.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.BUY),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                null,
                                BigMoney.of(buyPrice.getCurrencyUnit(), Constants.INIT_SELL_PRICE)),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                null,
                                BigMoney.zero(buyPrice.getCurrencyUnit())));

        //assertion
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder.getLeftCommission().isEqual(BigMoney.zero(buyPrice.getCurrencyUnit())), is(true));

        assertThat(sellOrder1.getCompleteDate(), nullValue());
        assertThat(sellOrder1.getLastTradedTime(), equalTo(sellOrder1.getLastTradedTime()));
        assertThat(sellOrder1.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(sellOrder1.getItemRemaining().isEqual(BigMoney.of(CurrencyUnit.of("BTC"), 1)), is(true));
        assertThat(sellOrder1.getLeftCommission().isEqual(BigMoney.of(CurrencyUnit.of("BTC"), 0.2602)), is(true));

        //verify
        verify(orderRepository).findPendingSellOrdersByPriceTime(eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100));

        verify(orderRepository).save(eq(buyOrder));
        verify(orderRepository, times(2)).save(any(Order.class));

        verify(commissionPolicyFactory, times(2)).createCommissionPolicy(any(Order.class));
        verify(policy).calculateBuyCommission(eq(buyOrder), eq(itemRemaining), eq(lowestSellPrice));
        verify(policy).calculateSellCommission(eq(sellOrder1), eq(itemRemaining), eq(lowestSellPrice));
    }

    private BigMoney getExecutedMoney(BigMoney amount, BigMoney price) {
        return getExecutedMoney(amount.getAmount(), price);
    }

    private BigMoney getExecutedMoney(BigDecimal amount, BigMoney price) {
        return Money.of(price.getCurrencyUnit(),
                amount.multiply(price.getAmount()),
                RoundingMode.HALF_EVEN).toBigMoney();
    }

    private Order createSellOrder(BigMoney price, BigMoney itemRemaining) {
        Order sellOrder = new Order(OrderType.SELL);
        sellOrder.setItemPrice(price);
        sellOrder.setItemRemaining(itemRemaining);
        sellOrder.setTransactionId(new TransactionId());
        sellOrder.setPrimaryKey(new OrderId().toString());

        return sellOrder;
    }

    private Order createSellOrder(BigMoney price, BigMoney itemRemaining, BigMoney commission) {
        Order sellOrder = new Order(OrderType.SELL);
        sellOrder.setItemPrice(price);
        sellOrder.setItemRemaining(itemRemaining);
        sellOrder.setLeftCommission(commission);
        sellOrder.setTransactionId(new TransactionId());
        sellOrder.setPrimaryKey(new OrderId().toString());

        return sellOrder;
    }
}