package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import org.joda.money.BigMoney;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/4/13
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SellOrderExecutorTest {
//    @Test
//    public void testExecuteNull() throws Exception {
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//
//        SellOrderExecutor executor = new SellOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//
//        //test
//        executor.executeSellOrder(null);
//
//        //verify
//        verify(orderBookRepository, never()).load(anyString());
//    }
//
//    @Test
//    public void testExecuteSellOrderPriceGeLowestSell() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal sellPrice = BigDecimal.valueOf(100.0021);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.002);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100.001);
//
//        SellOrder sellOrder = new SellOrder();
//        sellOrder.setOrderBookId(orderBookId);
//        sellOrder.setItemPrice(sellPrice);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        SellOrderExecutor executor = new SellOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//
//        //test
//        executor.executeSellOrder(sellOrder);
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook, never()).resetLowestSellPrice(any(SellOrder.class));
//    }
//
//    @Test
//    public void testExecuteSellOrderPriceHigherThanBuyPrices() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal sellPrice = BigDecimal.valueOf(100.1);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.8);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
//
//        SellOrder sellOrder = new SellOrder();
//        sellOrder.setOrderBookId(orderBookId);
//        sellOrder.setItemPrice(sellPrice);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        BuyOrderRepository sellOrderRepository = mock(BuyOrderRepository.class);
//
//        SellOrderExecutor executor = new SellOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//        executor.setBuyOrderRepository(sellOrderRepository);
//
//        //test
//        executor.executeSellOrder(sellOrder);
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook).resetLowestSellPrice(any(SellOrder.class));
//        verify(sellOrderRepository, never()).findDescPendingOrdersByPriceTime(
//                any(Date.class),
//                any(BigDecimal.class),
//                any(OrderBookId.class),
//                anyInt());
//    }
//
//    @Test
//    public void testExecuteSellOrderWithStrangeRepoReturns() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal sellPrice = BigDecimal.valueOf(99.1);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.8);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
//        Date placeDate = new Date();
//
//        SellOrder sellOrder = new SellOrder();
//        sellOrder.setOrderBookId(orderBookId);
//        sellOrder.setItemPrice(sellPrice);
//        sellOrder.setPlaceDate(placeDate);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        BuyOrderRepository sellOrderRepository = mock(BuyOrderRepository.class);
//        when(sellOrderRepository.findDescPendingOrdersByPriceTime(
//                eq(placeDate),
//                eq(sellPrice),
//                eq(orderBookId),
//                eq(100)
//        )).thenReturn(createBuyOrders(sellPrice.subtract(BigDecimal.valueOf(0.001)), sellPrice.subtract(BigDecimal.valueOf(1))));
//
//        SellOrderExecutor executor = new SellOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//        executor.setBuyOrderRepository(sellOrderRepository);
//
//        //execute
//        executor.executeSellOrder(sellOrder);
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook).resetLowestSellPrice(any(SellOrder.class));
//        verify(sellOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate),
//                eq(sellPrice),
//                eq(orderBookId),
//                eq(100));
//    }
//
//    private List<BuyOrder> createBuyOrders(BigDecimal... prices) {
//        ArrayList<BuyOrder> list = Lists.newArrayList();
//
//        for (int i = 0; i < prices.length; i++) {
//            BuyOrder sellOrder = new BuyOrder();
//            sellOrder.setItemPrice(prices[i]);
//            list.add(sellOrder);
//        }
//        return list;
//    }
//
//    @Test
//    public void testExecuteSellOrderWithBuyOrders() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal sellPrice = BigDecimal.valueOf(99.1);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(100.8);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
//        BigDecimal itemRemaining = BigDecimal.valueOf(100);
//        Date placeDate = new Date();
//
//        SellOrder sellOrder = new SellOrder();
//        sellOrder.setOrderBookId(orderBookId);
//        sellOrder.setItemPrice(sellPrice);
//        sellOrder.setPlaceDate(placeDate);
//        sellOrder.setItemRemaining(itemRemaining);
//
//
//        BuyOrder buyOrder1 = createBuyOrder(highestBuyPrice, itemRemaining.subtract(BigDecimal.TEN));
//        BuyOrder buyOrder2 = createBuyOrder(sellPrice, BigDecimal.TEN);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        BuyOrderRepository sellOrderRepository = mock(BuyOrderRepository.class);
//        when(sellOrderRepository.findDescPendingOrdersByPriceTime(
//                eq(placeDate),
//                eq(sellPrice),
//                eq(orderBookId),
//                eq(100)
//        )).thenReturn(Arrays.asList(buyOrder1,
//                buyOrder2));
//        when(sellOrderRepository.save(any(BuyOrder.class))).thenReturn(null);
//
//        SellOrderRepository buyOrderRepository = mock(SellOrderRepository.class);
//        when(buyOrderRepository.save(eq(sellOrder))).thenReturn(null);
//
//        SellOrderExecutor executor = new SellOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//        executor.setBuyOrderRepository(sellOrderRepository);
//        executor.setSellOrderRepository(buyOrderRepository);
//
//        //execute
//        executor.executeSellOrder(sellOrder);
//
//        //assertion
//        assertThat(sellOrder.getCompleteDate(), notNullValue());
//        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
//
//        assertThat(buyOrder1.getCompleteDate(), notNullValue());
//        assertThat(buyOrder1.getCompleteDate(), equalTo(buyOrder1.getLastTradedTime()));
//        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
//
//        assertThat(buyOrder2.getCompleteDate(), notNullValue());
//        assertThat(buyOrder2.getCompleteDate(), equalTo(buyOrder2.getLastTradedTime()));
//        assertThat(buyOrder2.getOrderStatus(), equalTo(OrderStatus.DONE));
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook).resetLowestSellPrice(any(SellOrder.class));
//        verify(sellOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate),
//                eq(sellPrice),
//                eq(orderBookId),
//                eq(100));
//
//        verify(buyOrderRepository, times(2)).save(eq(sellOrder));
//        verify(sellOrderRepository, times(2)).save(any(BuyOrder.class));
//
//        //verify values
//        ArgumentCaptor<BigDecimal> matchedAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
//        ArgumentCaptor<BigDecimal> matchedTradePriceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
//        verify(orderBook, times(2)).executeSelling(matchedAmountCaptor.capture(),
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
//        assertThat(matchedPrices, contains(highestBuyPrice, sellPrice));
//    }
//
//
//    @Test
//    public void testExecuteSellOrderWithOneExactBuyOrder() throws Exception {
//        OrderBookId orderBookId = new OrderBookId();
//        BigDecimal sellPrice = BigDecimal.valueOf(100);
//        BigDecimal lowestSellPrice = BigDecimal.valueOf(101);
//        BigDecimal highestBuyPrice = BigDecimal.valueOf(100);
//        BigDecimal itemRemaining = BigDecimal.valueOf(100);
//        Date placeDate = new Date();
//
//        SellOrder sellOrder = new SellOrder();
//        sellOrder.setOrderBookId(orderBookId);
//        sellOrder.setItemPrice(sellPrice);
//        sellOrder.setPlaceDate(placeDate);
//        sellOrder.setItemRemaining(itemRemaining);
//
//        BuyOrder buyOrder = createBuyOrder(sellPrice, itemRemaining);
//
//        OrderBook orderBook = mock(OrderBook.class);
//        when(orderBook.getLowestSellPrice()).thenReturn(lowestSellPrice);
//        when(orderBook.getHighestBuyPrice()).thenReturn(highestBuyPrice);
//
//        Repository<OrderBook> orderBookRepository = mock(Repository.class);
//        when(orderBookRepository.load(eq(orderBookId))).thenReturn(orderBook);
//
//        BuyOrderRepository sellOrderRepository = mock(BuyOrderRepository.class);
//        when(sellOrderRepository.findDescPendingOrdersByPriceTime(
//                eq(placeDate),
//                eq(sellPrice),
//                eq(orderBookId),
//                eq(100)))
//                .thenReturn(Arrays.asList(buyOrder));
//
//        when(sellOrderRepository.save(any(BuyOrder.class))).thenReturn(null);
//
//        SellOrderRepository buyOrderRepository = mock(SellOrderRepository.class);
//        when(buyOrderRepository.save(eq(sellOrder))).thenReturn(null);
//
//        SellOrderExecutor executor = new SellOrderExecutor();
//        executor.setOrderBookRepository(orderBookRepository);
//        executor.setBuyOrderRepository(sellOrderRepository);
//        executor.setSellOrderRepository(buyOrderRepository);
//
//        //execute
//        executor.executeSellOrder(sellOrder);
//
//        assertThat(sellOrder.getCompleteDate(), notNullValue());
//        assertThat(sellOrder.getCompleteDate(), equalTo(sellOrder.getLastTradedTime()));
//        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
//
//        assertThat(buyOrder.getCompleteDate(), notNullValue());
//        assertThat(buyOrder.getCompleteDate(), equalTo(buyOrder.getLastTradedTime()));
//        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));
//
//        //verify
//        verify(orderBookRepository).load(anyString());
//        verify(orderBook).resetLowestSellPrice(any(SellOrder.class));
//        verify(sellOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate),
//                eq(sellPrice),
//                eq(orderBookId),
//                eq(100));
//
//        verify(buyOrderRepository).save(eq(sellOrder));
//        verify(sellOrderRepository).save(any(BuyOrder.class));
//
//        //verify values
//        ArgumentCaptor<BigDecimal> matchedAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
//        ArgumentCaptor<BigDecimal> matchedTradePriceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
//        verify(orderBook).executeSelling(matchedAmountCaptor.capture(),
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
//        assertThat(matchedPrice, equalTo(sellPrice));
//    }

    private BuyOrder createBuyOrder(BigMoney price, BigMoney itemRemaining) {
        BuyOrder sellOrder = new BuyOrder();
        sellOrder.setItemPrice(price);
        sellOrder.setItemRemaining(itemRemaining);

        return sellOrder;
    }
}
