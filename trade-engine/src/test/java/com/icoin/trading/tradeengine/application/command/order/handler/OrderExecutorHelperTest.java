package com.icoin.trading.tradeengine.application.command.order.handler;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.commission.Commission;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.OrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/16/13
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderExecutorHelperTest {

    @Test
    public void testFindAscPendingOrdersByPriceTime() throws Exception {
        LocalDate placeDate = LocalDate.now();
        OrderBookId orderBookId = new OrderBookId();
        BigMoney price = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));

        OrderRepository sellOrderRepository = mock(OrderRepository.class);
        List<Order> sellOrders = createSellOrders(price.plus(BigDecimal.valueOf(0.01)), price.plus(BigDecimal.valueOf(1)));
        when(sellOrderRepository.findPendingSellOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(price),
                eq(orderBookId),
                eq(100))).thenReturn(sellOrders);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setOrderRepository(sellOrderRepository);

        List<Order> orders = helper.findAscPendingOrdersByPriceTime(
                placeDate.toDate(),
                price,
                orderBookId,
                100
        );

        assertThat(orders, equalTo(sellOrders));

        verify(sellOrderRepository).findPendingSellOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(price),
                eq(orderBookId),
                eq(100));
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
    public void testFindDescPendingOrdersByPriceTime() throws Exception {
        LocalDate placeDate = LocalDate.now();
        OrderBookId orderBookId = new OrderBookId();
        BigMoney price = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));

        OrderRepository buyOrderRepository = mock(OrderRepository.class);
        List<Order> buyOrders = createBuyOrders(price.plus(BigDecimal.valueOf(0.01)), price.plus(BigDecimal.valueOf(1)));

        when(buyOrderRepository.findPendingBuyOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(price),
                eq(orderBookId),
                eq(100))).thenReturn(buyOrders);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setOrderRepository(buyOrderRepository);

        List<Order> orders =
                helper.findDescPendingOrdersByPriceTime(
                        placeDate.toDate(),
                        price,
                        orderBookId,
                        100);

        assertThat(orders, equalTo(buyOrders));

        verify(buyOrderRepository).findPendingBuyOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(price),
                eq(orderBookId),
                eq(100));
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
    public void testFindSellOrder() throws Exception {
        OrderId orderId = new OrderId();
        BigMoney price = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));

        OrderRepository sellOrderRepository = mock(OrderRepository.class);
        Order sellOrder = createSellOrders(price.plus(BigDecimal.valueOf(7.01))).get(0);
        when(sellOrderRepository.findOne(eq(orderId.toString()))).thenReturn(sellOrder);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setOrderRepository(sellOrderRepository);

        Order order = helper.findSellOrder(orderId);

        assertThat(order, notNullValue());
        assertThat(order, equalTo(sellOrder));

        verify(sellOrderRepository).findOne(eq(orderId.toString()));
    }

    @Test
    public void testFindBuyOrder() throws Exception {
        OrderId orderId = new OrderId();
        BigMoney price = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));

        OrderRepository buyOrderRepository = mock(OrderRepository.class);
        Order buyOrder = createBuyOrders(price.plus(BigDecimal.valueOf(0.01))).get(0);

        when(buyOrderRepository.findOne(eq(orderId.toString()))).thenReturn(buyOrder);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setOrderRepository(buyOrderRepository);

        Order order = helper.findBuyOrder(orderId);

        assertThat(order, notNullValue());
        assertThat(order, equalTo(buyOrder));

        verify(buyOrderRepository).findOne(eq(orderId.toString()));
    }

    @Test
    public void testRefresh() throws Exception {
        OrderBookId orderBookId = new OrderBookId();
        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        BigMoney highestBuyPrice = lowestSellPrice.minus(1);


        OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.getOrderBookId()).thenReturn(orderBookId);
        when(orderBook.getCurrencyPair()).thenReturn(new CurrencyPair(Currencies.BTC, Currencies.AUD));

        OrderRepository orderRepository = mock(OrderRepository.class);
        Order buyOrder = createBuyOrders(highestBuyPrice.minus(BigDecimal.valueOf(0.01))).get(0);
        when(orderRepository.findHighestPricePendingBuyOrder(eq(orderBookId))).thenReturn(buyOrder);

        Order sellOrder = createSellOrders(lowestSellPrice.plus(BigDecimal.valueOf(7.01))).get(0);
        when(orderRepository.findLowestPricePendingSellOrder(eq(orderBookId))).thenReturn(sellOrder);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setOrderRepository(orderRepository);

        helper.refresh(orderBook);

        verify(orderBook).resetHighestBuyPrice(buyOrder.getPrimaryKey(), buyOrder.getItemPrice());
        verify(orderBook).resetLowestSellPrice(sellOrder.getPrimaryKey(), sellOrder.getItemPrice());
        verify(orderBook, atLeast(1)).getCurrencyPair();

        verify(orderRepository).findLowestPricePendingSellOrder(eq(orderBookId));
        verify(orderRepository).findHighestPricePendingBuyOrder(eq(orderBookId));
    }

    @Test
    public void testRefreshWithoutOrders() throws Exception {
        OrderBookId orderBookId = new OrderBookId();
        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, Constants.INIT_SELL_PRICE);
        BigMoney highestBuyPrice = BigMoney.zero(CurrencyUnit.AUD);


        OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.getOrderBookId()).thenReturn(orderBookId);
        when(orderBook.getCurrencyPair()).thenReturn(new CurrencyPair(Currencies.BTC, Currencies.AUD));

        OrderRepository orderRepository = mock(OrderRepository.class);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setOrderRepository(orderRepository);

        helper.refresh(orderBook);

        verify(orderBook).resetLowestSellPrice(isNull(String.class), eq(lowestSellPrice));
        verify(orderBook).resetHighestBuyPrice(isNull(String.class), eq(highestBuyPrice));
        verify(orderBook, atLeast(1)).getCurrencyPair();

        verify(orderRepository).findLowestPricePendingSellOrder(eq(orderBookId));
        verify(orderRepository).findHighestPricePendingBuyOrder(eq(orderBookId));
    }

    @Test
    public void testRecordTraded() throws Exception {
        BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(100.009));
        BigMoney price = BigMoney.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(100.009));
        BigMoney sellCommission = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(10.009));
        BigMoney buyCommission = BigMoney.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(1.009));

        OrderRepository orderRepository = mock(OrderRepository.class);
        Order buyOrder = new Order(OrderType.BUY);
        buyOrder.setItemRemaining(tradeAmount);
        buyOrder.setLeftCommission(buyCommission);
        buyOrder.setPrimaryKey("buyOrder");
        when(orderRepository.save(eq(buyOrder))).thenReturn(buyOrder);

        Order sellOrder = new Order(OrderType.SELL);
        sellOrder.setItemRemaining(tradeAmount.plus(10));
        sellOrder.setLeftCommission(sellCommission);
        sellOrder.setPrimaryKey("sellOrder");
        when(orderRepository.save(eq(sellOrder))).thenReturn(sellOrder);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setOrderRepository(orderRepository);

        //executing
        helper.recordTraded(buyOrder, sellOrder, buyCommission, sellCommission, tradeAmount, price, new Date());


        //verifying
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getCompleteDate(), nullValue());

        assertThat(buyOrder.getItemRemaining().getAmount(), is(closeTo(BigDecimal.ZERO, BigDecimal.valueOf(0.00000000001d))));
        assertThat(sellOrder.getItemRemaining().getAmount(), is(closeTo(BigDecimal.TEN, BigDecimal.valueOf(0.00000000001d))));
        assertThat(sellOrder.getLeftCommission().getAmount(), is(closeTo(BigDecimal.valueOf(0), BigDecimal.valueOf(0.00000000001d))));
        assertThat(buyOrder.getLeftCommission().getAmount(), is(closeTo(BigDecimal.valueOf(0), BigDecimal.valueOf(0.00000000001d))));

        verify(orderRepository).save(eq(buyOrder));
        verify(orderRepository).save(eq(sellOrder));
    }

    @Test
    public void testCalcExecutedBuyCommission() throws Exception {
        final BigMoney price = BigMoney.of(CurrencyUnit.of(Currencies.CNY), 100);
        final BigMoney amount = BigMoney.of(CurrencyUnit.of(Currencies.CNY), 10);
        final Order buyOrder = new Order(OrderType.BUY);

        final CommissionPolicyFactory factory = mock(CommissionPolicyFactory.class);
        final CommissionPolicy policy = mock(CommissionPolicy.class);
        final Commission commission = new Commission(BigMoney.of(CurrencyUnit.EUR, 10), "");
        when(policy.calculateBuyCommission(eq(buyOrder), eq(amount), eq(price))).thenReturn(commission);

        when(factory.createCommissionPolicy(eq(buyOrder))).thenReturn(policy);


        final OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setCommissionPolicyFactory(factory);


        final BigMoney commissionMoney =
                helper.calcExecutedBuyCommission(
                        buyOrder,
                        price,
                        amount);

        assertThat(commissionMoney.isEqual(commission.getBigMoneyCommission()), is(true));

        verify(factory).createCommissionPolicy(eq(buyOrder));
        verify(policy).calculateBuyCommission(eq(buyOrder), eq(amount), eq(price));
    }

    @Test
    public void testCalcExecutedSellCommission() throws Exception {
        final BigMoney price = BigMoney.of(CurrencyUnit.of(Currencies.CNY), 100);
        final BigMoney amount = BigMoney.of(CurrencyUnit.of(Currencies.CNY), 10);
        final Order sellOrder = new Order(OrderType.BUY);

        final CommissionPolicyFactory factory = mock(CommissionPolicyFactory.class);
        final CommissionPolicy policy = mock(CommissionPolicy.class);
        final Commission commission = new Commission(BigMoney.of(CurrencyUnit.EUR, 10), "");
        when(policy.calculateSellCommission(eq(sellOrder), eq(amount), eq(price))).thenReturn(commission);

        when(factory.createCommissionPolicy(eq(sellOrder))).thenReturn(policy);


        final OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setCommissionPolicyFactory(factory);


        final BigMoney commissionMoney =
                helper.calcExecutedSellCommission(
                        sellOrder,
                        price,
                        amount);

        assertThat(commissionMoney.isEqual(commission.getBigMoneyCommission()), is(true));

        verify(factory).createCommissionPolicy(eq(sellOrder));
        verify(policy).calculateSellCommission(eq(sellOrder), eq(amount), eq(price));
    }
}