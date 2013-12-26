package com.icoin.trading.tradeengine.application.command.order.handler;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.notNull;
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

        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
        List<SellOrder> sellOrders = createSellOrders(price.plus(BigDecimal.valueOf(0.01)), price.plus(BigDecimal.valueOf(1)));
        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(price),
                eq(orderBookId),
                eq(100))).thenReturn(sellOrders);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setSellOrderRepository(sellOrderRepository);

        List<SellOrder> orders = helper.findAscPendingOrdersByPriceTime(
                placeDate.toDate(),
                price,
                orderBookId,
                100
        );

        assertThat(orders, equalTo(sellOrders));

        verify(sellOrderRepository).findAscPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(price),
                eq(orderBookId),
                eq(100));
    }


    private List<SellOrder> createSellOrders(BigMoney... prices) {
        ArrayList<SellOrder> list = Lists.newArrayList();

        for (int i = 0; i < prices.length; i++) {
            SellOrder sellOrder = new SellOrder();
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

        BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
        List<BuyOrder> buyOrders = createBuyOrders(price.plus(BigDecimal.valueOf(0.01)), price.plus(BigDecimal.valueOf(1)));

        when(buyOrderRepository.findDescPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(price),
                eq(orderBookId),
                eq(100))).thenReturn(buyOrders);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setBuyOrderRepository(buyOrderRepository);

        List<BuyOrder> orders =
                helper.findDescPendingOrdersByPriceTime(
                        placeDate.toDate(),
                        price,
                        orderBookId,
                        100);

        assertThat(orders, equalTo(buyOrders));

        verify(buyOrderRepository).findDescPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(price),
                eq(orderBookId),
                eq(100));
    }

    private List<BuyOrder> createBuyOrders(BigMoney... prices) {
        ArrayList<BuyOrder> list = Lists.newArrayList();

        for (int i = 0; i < prices.length; i++) {
            BuyOrder buyOrder = new BuyOrder();
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

        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
        SellOrder sellOrder = createSellOrders(price.plus(BigDecimal.valueOf(7.01))).get(0);
        when(sellOrderRepository.findOne(eq(orderId.toString()))).thenReturn(sellOrder);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setSellOrderRepository(sellOrderRepository);

        SellOrder order = helper.findSellOrder(orderId);

        assertThat(order, notNullValue());
        assertThat(order, equalTo(sellOrder));

        verify(sellOrderRepository).findOne(eq(orderId.toString()));
    }

    @Test
    public void testFindBuyOrder() throws Exception {
        OrderId orderId = new OrderId();
        BigMoney price = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));

        BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
        BuyOrder buyOrder = createBuyOrders(price.plus(BigDecimal.valueOf(0.01))).get(0);

        when(buyOrderRepository.findOne(eq(orderId.toString()))).thenReturn(buyOrder);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setBuyOrderRepository(buyOrderRepository);

        BuyOrder order = helper.findBuyOrder(orderId);

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
        when(orderBook.getCurrencyPair()).thenReturn(new CurrencyPair(Currencies.BTC,Currencies.AUD));

        BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
        BuyOrder buyOrder = createBuyOrders(highestBuyPrice.minus(BigDecimal.valueOf(0.01))).get(0);
        when(buyOrderRepository.findHighestPricePendingOrder(eq(orderBookId))).thenReturn(buyOrder);

        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
        SellOrder sellOrder = createSellOrders(lowestSellPrice.plus(BigDecimal.valueOf(7.01))).get(0);
        when(sellOrderRepository.findLowestPricePendingOrder(eq(orderBookId))).thenReturn(sellOrder);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setSellOrderRepository(sellOrderRepository);
        helper.setBuyOrderRepository(buyOrderRepository);

        helper.refresh(orderBook);

        verify(orderBook).resetHighestBuyPrice(buyOrder.getPrimaryKey(), buyOrder.getItemPrice());
        verify(orderBook).resetLowestSellPrice(sellOrder.getPrimaryKey(), sellOrder.getItemPrice());
        verify(orderBook, atLeast(1)).getCurrencyPair();

        verify(sellOrderRepository).findLowestPricePendingOrder(eq(orderBookId));
        verify(buyOrderRepository).findHighestPricePendingOrder(eq(orderBookId));
    }

    @Test
    public void testRefreshWithoutOrders() throws Exception {
        OrderBookId orderBookId = new OrderBookId();
        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, Constants.INIT_SELL_PRICE);
        BigMoney highestBuyPrice = BigMoney.zero(CurrencyUnit.AUD);


        OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.getOrderBookId()).thenReturn(orderBookId);
        when(orderBook.getCurrencyPair()).thenReturn(new CurrencyPair(Currencies.BTC,Currencies.AUD));

        BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setSellOrderRepository(sellOrderRepository);
        helper.setBuyOrderRepository(buyOrderRepository);

        helper.refresh(orderBook);

        verify(orderBook).resetLowestSellPrice(isNull(String.class), eq(lowestSellPrice));
        verify(orderBook).resetHighestBuyPrice(isNull(String.class), eq(highestBuyPrice));
        verify(orderBook, atLeast(1)).getCurrencyPair();

        verify(sellOrderRepository).findLowestPricePendingOrder(eq(orderBookId));
        verify(buyOrderRepository).findHighestPricePendingOrder(eq(orderBookId));
    }

    @Test
    public void testRecordTraded() throws Exception {
        BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(100.009));
        BigMoney price = BigMoney.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(100.009));
        BigMoney sellCommission = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(10.009));
        BigMoney buyCommission = BigMoney.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(1.009));

        BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
        BuyOrder buyOrder = new BuyOrder();
        buyOrder.setItemRemaining(tradeAmount);
        when(buyOrderRepository.save(eq(buyOrder))).thenReturn(buyOrder);

        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
        SellOrder sellOrder = new SellOrder();
        sellOrder.setItemRemaining(tradeAmount.plus(10));
        when(sellOrderRepository.save(eq(sellOrder))).thenReturn(sellOrder);

        OrderExecutorHelper helper = new OrderExecutorHelper();
        helper.setBuyOrderRepository(buyOrderRepository);
        helper.setSellOrderRepository(sellOrderRepository);

        //executing
        helper.recordTraded(buyOrder, sellOrder, buyCommission, sellCommission, tradeAmount, price, new Date());


        //verifying
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getCompleteDate(), nullValue());

        assertThat(buyOrder.getItemRemaining().getAmount(), is(closeTo(BigDecimal.ZERO, BigDecimal.valueOf(0.00000000001d))));
        assertThat(sellOrder.getItemRemaining().getAmount(), is(closeTo(BigDecimal.TEN, BigDecimal.valueOf(0.00000000001d))));
        assertThat(sellOrder.getTotalCommission().getAmount(), is(closeTo(BigDecimal.valueOf(10.009), BigDecimal.valueOf(0.00000000001d))));
        assertThat(buyOrder.getTotalCommission().getAmount(), is(closeTo(BigDecimal.valueOf(1.009), BigDecimal.valueOf(0.00000000001d))));

        verify(buyOrderRepository).save(eq(buyOrder));
        verify(sellOrderRepository).save(eq(sellOrder));
    }
}