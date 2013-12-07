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
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
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
public class BuyOrderExecutorTest {
//    @Test
//    public void testExecuteNull() throws Exception {
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//
//        BuyOrderExecutor executor = new BuyOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//
//        //test
//        executor.executeBuyOrder(null);
//
//        //verify
//        verify(orderBookRepository, never()).load(anyString());
//    }
//
//    @Test
//    public void testExecuteBuyOrderPriceLeHighestBuy() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal buyPrice = BigDecimal.valueOf(100.0009);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.002);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100.001);
//
//        BuyOrder buyOrder = new BuyOrder();
//        buyOrder.setOrderBookId(orderBookId);
//        buyOrder.setItemPrice(buyPrice);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        BuyOrderExecutor executor = new BuyOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//
//        //test
//        executor.executeBuyOrder(buyOrder);
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook, never()).resetHighestBuyPrice(any(BuyOrder.class));
//    }
//
//    @Test
//    public void testExecuteBuyOrderPriceLowerThanSellPrices() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal buyPrice = BigDecimal.valueOf(100.0009);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.002);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
//
//        BuyOrder buyOrder = new BuyOrder();
//        buyOrder.setOrderBookId(orderBookId);
//        buyOrder.setItemPrice(buyPrice);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
//
//        BuyOrderExecutor executor = new BuyOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//        executor.setSellOrderRepository(sellOrderRepository);
//
//
//        //test
//        executor.executeBuyOrder(buyOrder);
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook).resetHighestBuyPrice(any(BuyOrder.class));
//        verify(sellOrderRepository, never()).findAscPendingOrdersByPriceTime(
//                any(Date.class),
//                any(BigDecimal.class),
//                any(OrderBookId.class),
//                anyInt());
//    }
//
//    @Test
//    public void testExecuteBuyOrderWithStrangeRepoReturns() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal buyPrice = BigDecimal.valueOf(100.1);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.002);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
//        Date placeDate = new Date();
//
//        BuyOrder buyOrder = new BuyOrder();
//        buyOrder.setOrderBookId(orderBookId);
//        buyOrder.setItemPrice(buyPrice);
//        buyOrder.setPlaceDate(placeDate);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
//        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
//                eq(placeDate),
//                eq(buyPrice),
//                eq(orderBookId),
//                eq(100)
//        )).thenReturn(createSellOrders(buyPrice.add(BigDecimal.valueOf(0.001)), buyPrice.add(BigDecimal.valueOf(1))));
//
//        BuyOrderExecutor executor = new BuyOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//        executor.setSellOrderRepository(sellOrderRepository);
//
//        //execute
//        executor.executeBuyOrder(buyOrder);
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook).resetHighestBuyPrice(any(BuyOrder.class));
//        verify(sellOrderRepository).findAscPendingOrdersByPriceTime(eq(placeDate),
//                eq(buyPrice),
//                eq(orderBookId),
//                eq(100));
//    }
//
//    private List<SellOrder> createSellOrders(BigDecimal... prices) {
//        ArrayList<SellOrder> list = Lists.newArrayList();
//
//        for (int i = 0; i < prices.length; i++) {
//            SellOrder sellOrder = new SellOrder();
//            sellOrder.setItemPrice(prices[i]);
//            list.add(sellOrder);
//        }
//        return list;
//    }
//
//    @Test
//    public void testExecuteBuyOrderWithSellOrders() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal buyPrice = BigDecimal.valueOf(100.1);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.01);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
//        BigDecimal itemRemaining = BigDecimal.valueOf(100);
//        Date placeDate = new Date();
//
//        BuyOrder buyOrder = new BuyOrder();
//        buyOrder.setOrderBookId(orderBookId);
//        buyOrder.setItemPrice(buyPrice);
//        buyOrder.setPlaceDate(placeDate);
//        buyOrder.setItemRemaining(itemRemaining);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
//        SellOrder sellOrder1 = createSellOrder(highestBuyPrice, itemRemaining.subtract(BigDecimal.TEN));
//        SellOrder sellOrder2 = createSellOrder(buyPrice, itemRemaining.add(BigDecimal.valueOf(100)));
//        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
//                eq(placeDate),
//                eq(buyPrice),
//                eq(orderBookId),
//                eq(100)
//        )).thenReturn(Arrays.asList(sellOrder1,
//                sellOrder2));
//        when(sellOrderRepository.save(any(SellOrder.class))).thenReturn(null);
//
//        BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
//        when(buyOrderRepository.save(eq(buyOrder))).thenReturn(null);
//
//        BuyOrderExecutor executor = new BuyOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//        executor.setSellOrderRepository(sellOrderRepository);
//        executor.setBuyOrderRepository(buyOrderRepository);
//
//        //execute
//        executor.executeBuyOrder(buyOrder);
//
//        //assertion
//        assertThat(buyOrder.getCompleteDate(), notNullValue());
//        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
//
//        assertThat(sellOrder1.getCompleteDate(), notNullValue());
//        assertThat(sellOrder1.getCompleteDate(), equalTo(sellOrder1.getLastTradedTime()));
//        assertThat(sellOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
//
//        assertThat(sellOrder2.getCompleteDate(), nullValue());
//        assertThat(sellOrder2.getLastTradedTime(), notNullValue());
//        assertThat(sellOrder2.getOrderStatus(), equalTo(OrderStatus.PENDING));
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook).resetHighestBuyPrice(any(BuyOrder.class));
//        verify(sellOrderRepository).findAscPendingOrdersByPriceTime(eq(placeDate),
//                eq(buyPrice),
//                eq(orderBookId),
//                eq(100));
//
//        verify(buyOrderRepository, times(2)).save(eq(buyOrder));
//        verify(sellOrderRepository, times(2)).save(any(SellOrder.class));
//
//        //verify values
//        ArgumentCaptor<BigDecimal> matchedAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
//        ArgumentCaptor<BigDecimal> matchedTradePriceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
//        verify(orderBook, times(2)).executeBuying(matchedAmountCaptor.capture(),
//                matchedTradePriceCaptor.capture(),
//                anyString(),
//                anyString(),
//                any(TransactionId.class),
//                any(TransactionId.class),
//                any(Date.class));
//
//        List<BigDecimal> matchedAmounts = matchedAmountCaptor.getAllValues();
//        List<BigDecimal> matchedPrices = matchedTradePriceCaptor.getAllValues();
//
//        assertThat(matchedAmounts, hasSize(2));
//        assertThat(matchedAmounts, contains(itemRemaining.subtract(BigDecimal.TEN), BigDecimal.TEN));
//        assertThat(matchedPrices, hasSize(2));
//        assertThat(matchedPrices, contains(highestBuyPrice, buyPrice));
//    }
//
//
//    @Test
//    public void testExecuteBuyOrderWithOneExactSellOrder() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal buyPrice = BigDecimal.valueOf(100.1);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.01);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
//        BigDecimal itemRemaining = BigDecimal.valueOf(100);
//        Date placeDate = new Date();
//
//        BuyOrder buyOrder = new BuyOrder();
//        buyOrder.setOrderBookId(orderBookId);
//        buyOrder.setItemPrice(buyPrice);
//        buyOrder.setPlaceDate(placeDate);
//        buyOrder.setItemRemaining(itemRemaining);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
//        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
//                eq(placeDate),
//                eq(buyPrice),
//                eq(orderBookId),
//                eq(100)))
//                .thenReturn(Arrays.asList(createSellOrder(buyPrice, itemRemaining)));
//
//        when(sellOrderRepository.save(any(SellOrder.class))).thenReturn(null);
//
//        BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
//        when(buyOrderRepository.save(eq(buyOrder))).thenReturn(null);
//
//        BuyOrderExecutor executor = new BuyOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//        executor.setSellOrderRepository(sellOrderRepository);
//        executor.setBuyOrderRepository(buyOrderRepository);
//
//        //execute
//        executor.executeBuyOrder(buyOrder);
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook).resetHighestBuyPrice(any(BuyOrder.class));
//        verify(sellOrderRepository).findAscPendingOrdersByPriceTime(
//                eq(placeDate),
//                eq(buyPrice),
//                eq(orderBookId),
//                eq(100));
//
//        verify(buyOrderRepository).save(eq(buyOrder));
//        verify(sellOrderRepository).save(any(SellOrder.class));
//
//        //verify values
//        ArgumentCaptor<BigDecimal> matchedAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
//        ArgumentCaptor<BigDecimal> matchedTradePriceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
//        verify(orderBook).executeBuying(matchedAmountCaptor.capture(),
//                matchedTradePriceCaptor.capture(),
//                anyString(),
//                anyString(),
//                any(TransactionId.class),
//                any(TransactionId.class),
//                any(Date.class));
//
//        BigDecimal matchedAmount = matchedAmountCaptor.getValue();
//        BigDecimal matchedPrice = matchedTradePriceCaptor.getValue();
//
//        assertThat(matchedAmount, equalTo(itemRemaining));
//        assertThat(matchedPrice, equalTo(buyPrice));
//    }
//
//    private SellOrder createSellOrder(BigDecimal price, BigDecimal itemRemaining) {
//        SellOrder sellOrder = new SellOrder();
//        sellOrder.setItemPrice(price);
//        sellOrder.setItemRemaining(itemRemaining);
//
//        return sellOrder;
//    }
}

