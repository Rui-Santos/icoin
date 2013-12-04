package com.icoin.trading.tradeengine.application.executor;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.repository.Repository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
public class SellOrderExecutorTest {
    @Test
    public void testExecuteNull() throws Exception {
        Repository<OrderBook> orderBookRepository = mock(Repository.class);

        SellOrderExecutor executor = new SellOrderExecutor();
        executor.setOrderBookRepository(orderBookRepository);

        //test
        executor.execute(null);

        //verify
        verify(orderBookRepository, never()).load(anyString());
    }

    @Test
    public void testExecuteSellOrderPriceGeLowestSell() throws Exception {
        OrderBookId orderBookId = new OrderBookId();
        BigDecimal sellPrice = BigDecimal.valueOf(100.0021);
        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.002);
        BigDecimal highestBuyPrice = BigDecimal.valueOf(100.001);

        SellOrder sellOrder = new SellOrder();
        sellOrder.setOrderBookId(orderBookId);
        sellOrder.setItemPrice(sellPrice);

        OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);

        Repository<OrderBook> orderBookRepository = mock(Repository.class);
        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);

        SellOrderExecutor executor = new SellOrderExecutor();
        executor.setOrderBookRepository(orderBookRepository);

        //test
        executor.execute(sellOrder);

        //verify
        verify(orderBookRepository).load(anyString());
        verify(orderBook, never()).resetLowestSellPrice(any(SellOrder.class));
    }

    @Test
    public void testExecuteSellOrderPriceHigherThanBuyPrices() throws Exception {
        OrderBookId orderBookId = new OrderBookId();
        BigDecimal sellPrice = BigDecimal.valueOf(100.1);
        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.8);
        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);

        SellOrder sellOrder = new SellOrder();
        sellOrder.setOrderBookId(orderBookId);
        sellOrder.setItemPrice(sellPrice);

        OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);

        Repository<OrderBook> orderBookRepository = mock(Repository.class);
        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);

        BuyOrderRepository sellOrderRepository = mock(BuyOrderRepository.class);

        SellOrderExecutor executor = new SellOrderExecutor();
        executor.setOrderBookRepository(orderBookRepository);
        executor.setBuyOrderRepository(sellOrderRepository);

        //test
        executor.execute(sellOrder);

        //verify
        verify(orderBookRepository).load(anyString());
        verify(orderBook).resetLowestSellPrice(any(SellOrder.class));
        verify(sellOrderRepository, never()).findDescPendingOrdersByPriceTime(
                any(Date.class),
                any(BigDecimal.class),
                any(OrderBookId.class),
                anyInt());
    }

    @Test
    public void testExecuteSellOrderWithStrangeRepoReturns() throws Exception {
        OrderBookId orderBookId = new OrderBookId();
        BigDecimal sellPrice = BigDecimal.valueOf(99.1);
        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.8);
        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
        Date placeDate = new Date();

        SellOrder sellOrder = new SellOrder();
        sellOrder.setOrderBookId(orderBookId);
        sellOrder.setItemPrice(sellPrice);
        sellOrder.setPlaceDate(placeDate);

        OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);

        Repository<OrderBook> orderBookRepository = mock(Repository.class);
        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);

        BuyOrderRepository sellOrderRepository = mock(BuyOrderRepository.class);
        when(sellOrderRepository.findDescPendingOrdersByPriceTime(
                eq(placeDate),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(createBuyOrders(sellPrice.subtract(BigDecimal.valueOf(0.001)), sellPrice.subtract(BigDecimal.valueOf(1))));

        SellOrderExecutor executor = new SellOrderExecutor();
        executor.setOrderBookRepository(orderBookRepository);
        executor.setBuyOrderRepository(sellOrderRepository);

        //execute
        executor.execute(sellOrder);

        //verify
        verify(orderBookRepository).load(anyString());
        verify(orderBook).resetLowestSellPrice(any(SellOrder.class));
        verify(sellOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));
    }

    private List<BuyOrder> createBuyOrders(BigDecimal... prices) {
        ArrayList<BuyOrder> list = Lists.newArrayList();

        for (int i = 0; i < prices.length; i++) {
            BuyOrder sellOrder = new BuyOrder();
            sellOrder.setItemPrice(prices[i]);
            list.add(sellOrder);
        }
        return list;
    }

    @Test
    public void testExecuteSellOrderWithBuyOrders() throws Exception {
        OrderBookId orderBookId = new OrderBookId();
        BigDecimal sellPrice = BigDecimal.valueOf(99.1);
        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.8);
        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
        BigDecimal itemsRemaining = BigDecimal.valueOf(100);
        Date placeDate = new Date();

        SellOrder sellOrder = new SellOrder();
        sellOrder.setOrderBookId(orderBookId);
        sellOrder.setItemPrice(sellPrice);
        sellOrder.setPlaceDate(placeDate);
        sellOrder.setItemsRemaining(itemsRemaining);


        BuyOrder buyOrder1 = createBuyOrder(highestBuyPrice, itemsRemaining.subtract(BigDecimal.TEN));
        BuyOrder buyOrder2 = createBuyOrder(sellPrice, BigDecimal.TEN);

        OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);

        Repository<OrderBook> orderBookRepository = mock(Repository.class);
        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);

        BuyOrderRepository sellOrderRepository = mock(BuyOrderRepository.class);
        when(sellOrderRepository.findDescPendingOrdersByPriceTime(
                eq(placeDate),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(buyOrder1,
                buyOrder2));
        when(sellOrderRepository.save(any(BuyOrder.class))).thenReturn(null);

        SellOrderRepository buyOrderRepository = mock(SellOrderRepository.class);
        when(buyOrderRepository.save(eq(sellOrder))).thenReturn(null);

        SellOrderExecutor executor = new SellOrderExecutor();
        executor.setOrderBookRepository(orderBookRepository);
        executor.setBuyOrderRepository(sellOrderRepository);
        executor.setSellOrderRepository(buyOrderRepository);

        //execute
        executor.execute(sellOrder);

        //assertion
        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder1.getCompleteDate(), notNullValue());
        assertThat(buyOrder1.getCompleteDate(), equalTo(buyOrder1.getLastTradedTime()));
        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder2.getCompleteDate(), notNullValue());
        assertThat(buyOrder2.getCompleteDate(), equalTo(buyOrder2.getLastTradedTime()));
        assertThat(buyOrder2.getOrderStatus(), equalTo(OrderStatus.DONE));

        //verify
        verify(orderBookRepository).load(anyString());
        verify(orderBook).resetLowestSellPrice(any(SellOrder.class));
        verify(sellOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));

        verify(buyOrderRepository, times(2)).save(eq(sellOrder));
        verify(sellOrderRepository, times(2)).save(any(BuyOrder.class));

        //verify values
        ArgumentCaptor<BigDecimal> matchedAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> matchedTradePriceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(orderBook, times(2)).executeSelling(matchedAmountCaptor.capture(),
                matchedTradePriceCaptor.capture(),
                anyString(),
                anyString(),
                any(TransactionId.class),
                any(TransactionId.class),
                any(Date.class));

        List<BigDecimal> matchedAmounts = matchedAmountCaptor.getAllValues();
        List<BigDecimal> matchedPrices = matchedTradePriceCaptor.getAllValues();

        assertThat(matchedAmounts, hasSize(2));
        assertThat(matchedAmounts, contains(itemsRemaining.subtract(BigDecimal.TEN), BigDecimal.TEN));
        assertThat(matchedPrices, hasSize(2));
        assertThat(matchedPrices, contains(highestBuyPrice, sellPrice));
    }


    @Test
    public void testExecuteSellOrderWithOneExactBuyOrder() throws Exception {
        OrderBookId orderBookId = new OrderBookId();
        BigDecimal sellPrice = BigDecimal.valueOf(100);
        BigDecimal lowestSellPrice = BigDecimal.valueOf(101);
        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
        BigDecimal itemsRemaining = BigDecimal.valueOf(100);
        Date placeDate = new Date();

        SellOrder sellOrder = new SellOrder();
        sellOrder.setOrderBookId(orderBookId);
        sellOrder.setItemPrice(sellPrice);
        sellOrder.setPlaceDate(placeDate);
        sellOrder.setItemsRemaining(itemsRemaining);

        BuyOrder buyOrder = createBuyOrder(sellPrice, itemsRemaining);

        OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);

        Repository<OrderBook> orderBookRepository = mock(Repository.class);
        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);

        BuyOrderRepository sellOrderRepository = mock(BuyOrderRepository.class);
        when(sellOrderRepository.findDescPendingOrdersByPriceTime(
                eq(placeDate),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)))
                .thenReturn(Arrays.asList(buyOrder));

        when(sellOrderRepository.save(any(BuyOrder.class))).thenReturn(null);

        SellOrderRepository buyOrderRepository = mock(SellOrderRepository.class);
        when(buyOrderRepository.save(eq(sellOrder))).thenReturn(null);

        SellOrderExecutor executor = new SellOrderExecutor();
        executor.setOrderBookRepository(orderBookRepository);
        executor.setBuyOrderRepository(sellOrderRepository);
        executor.setSellOrderRepository(buyOrderRepository);

        //execute
        executor.execute(sellOrder);

        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getCompleteDate(), equalTo(sellOrder.getLastTradedTime()));
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getCompleteDate(), equalTo(buyOrder.getLastTradedTime()));
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        //verify
        verify(orderBookRepository).load(anyString());
        verify(orderBook).resetLowestSellPrice(any(SellOrder.class));
        verify(sellOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));

        verify(buyOrderRepository).save(eq(sellOrder));
        verify(sellOrderRepository).save(any(BuyOrder.class));

        //verify values
        ArgumentCaptor<BigDecimal> matchedAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> matchedTradePriceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(orderBook).executeSelling(matchedAmountCaptor.capture(),
                matchedTradePriceCaptor.capture(),
                anyString(),
                anyString(),
                any(TransactionId.class),
                any(TransactionId.class),
                any(Date.class));

        BigDecimal matchedAmount = matchedAmountCaptor.getValue();
        BigDecimal matchedPrice = matchedTradePriceCaptor.getValue();

        assertThat(matchedAmount, equalTo(itemsRemaining));
        assertThat(matchedPrice, equalTo(sellPrice));
    }

    private BuyOrder createBuyOrder(BigDecimal price, BigDecimal itemsRemaining) {
        BuyOrder sellOrder = new BuyOrder();
        sellOrder.setItemPrice(price);
        sellOrder.setItemsRemaining(itemsRemaining);

        return sellOrder;
    }
}
