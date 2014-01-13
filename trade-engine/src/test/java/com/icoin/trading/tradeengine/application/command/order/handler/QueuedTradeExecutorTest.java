package com.icoin.trading.tradeengine.application.command.order.handler;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.OrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class QueuedTradeExecutorTest {
    private final OrderBookId orderBookId = new OrderBookId();
    private final OrderBookId reInitedOrderBookId = new OrderBookId();
    private CommandGateway commandGateway = mock(CommandGateway.class);
    private OrderBookQueryRepository orderBookQueryRepository = mock(OrderBookQueryRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private final Random random = new Random();
    private int count = 0;

    @Before
    public void setUp() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TimeUnit.MILLISECONDS.sleep(10 + random.nextInt(200));
                count++;
                return null;
            }
        }).when(commandGateway).send(anyObject());
    }

    @Test(timeout = 30000L)
    public void testExecution() throws Exception {


        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks(orderBookId));
        QueuedTradeExecutor executor = new QueuedTradeExecutor(orderBookQueryRepository, commandGateway, orderRepository);

        executor.start();

        int sellOrderQuantity = random.nextInt(10);
        int buyOrderQuantity = random.nextInt(10);

        Thread sell = new Thread(new SellProducer(executor, sellOrderQuantity, orderBookId));
        sell.start();
        Thread buy = new Thread(new BuyProducer(executor, buyOrderQuantity, orderBookId));
        buy.start();


        sell.join();
        buy.join();

        while (count < sellOrderQuantity + buyOrderQuantity) {
            TimeUnit.MILLISECONDS.sleep(50);
        }

        assertThat(count, equalTo(sellOrderQuantity + buyOrderQuantity));

        verify(commandGateway, times(count)).send(anyObject());
    }

    @Test(timeout = 30000L)
    public void testExecutionWithOrderInitialization() throws Exception {
        final Order highestBuy = new Order(OrderType.BUY);
        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.EUR, 10);
        highestBuy.setItemPrice(highestBuyPrice);
        when(orderRepository.findHighestPricePendingBuyOrder(eq(orderBookId)))
                .thenReturn(highestBuy);

        final Order lowestSell = new Order(OrderType.SELL);
        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.EUR, 9);
        lowestSell.setItemPrice(lowestSellPrice);
        when(orderRepository.findLowestPricePendingSellOrder(eq(orderBookId)))
                .thenReturn(lowestSell);

        OrderBookEntry reinitializedOrderBook = new OrderBookEntry();
        final Date lastTradedTime = new Date();
        reinitializedOrderBook.setLastTradedTime(lastTradedTime);
        reinitializedOrderBook.setPrimaryKey(orderBookId.toString());
        when(orderBookQueryRepository.findOne(eq(orderBookId.toString())))
                .thenReturn(reinitializedOrderBook);

        when(orderRepository.findPlacedPendingOrdersAfter(eq(lastTradedTime), eq(orderBookId), eq(100)))
                .thenReturn(createOrders(10, orderBookId), null);


        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks(orderBookId));
        QueuedTradeExecutor executor = new QueuedTradeExecutor(orderBookQueryRepository, commandGateway, orderRepository);

        executor.start();

        int sellOrderQuantity = random.nextInt(10);
        int buyOrderQuantity = random.nextInt(10);

        Thread sell = new Thread(new SellProducer(executor, sellOrderQuantity, orderBookId));
        sell.start();
        Thread buy = new Thread(new BuyProducer(executor, buyOrderQuantity, orderBookId));
        buy.start();


        sell.join();
        buy.join();

        while (count < sellOrderQuantity + buyOrderQuantity + 10) {
            TimeUnit.MILLISECONDS.sleep(50);
        }

        assertThat(count, equalTo(sellOrderQuantity + buyOrderQuantity + 10));

        verify(commandGateway, times(count)).send(anyObject());
    }

    @Test(timeout = 60000L)
    public void testReinitialize() throws Exception {
        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks(orderBookId),
                createOrderBooks(reInitedOrderBookId));
        QueuedTradeExecutor executor = new QueuedTradeExecutor(orderBookQueryRepository, commandGateway, orderRepository);
        executor.start();

        int sellOrderQuantity = random.nextInt(10);
        int buyOrderQuantity = random.nextInt(10);

        Thread sell = new Thread(new SellProducer(executor, sellOrderQuantity, orderBookId));
        sell.start();
        Thread buy = new Thread(new BuyProducer(executor, buyOrderQuantity, orderBookId));
        buy.start();


        sell.join();
        buy.join();

        while (count < sellOrderQuantity + buyOrderQuantity) {
            TimeUnit.MILLISECONDS.sleep(50);
        }


        //prepare re-initializaton
        int preCount = count;
        final Order highestBuy = new Order(OrderType.BUY);
        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.EUR, 10);
        highestBuy.setItemPrice(highestBuyPrice);
        when(orderRepository.findHighestPricePendingBuyOrder(eq(reInitedOrderBookId)))
                .thenReturn(highestBuy);

        final Order lowestSell = new Order(OrderType.SELL);
        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.EUR, 9);
        lowestSell.setItemPrice(lowestSellPrice);
        when(orderRepository.findLowestPricePendingSellOrder(eq(reInitedOrderBookId)))
                .thenReturn(lowestSell);

        OrderBookEntry reinitializedOrderBook = new OrderBookEntry();
        final Date lastTradedTime = new Date();
        reinitializedOrderBook.setLastTradedTime(lastTradedTime);
        reinitializedOrderBook.setPrimaryKey(reInitedOrderBookId.toString());
        when(orderBookQueryRepository.findOne(eq(reInitedOrderBookId.toString())))
                .thenReturn(reinitializedOrderBook);

        when(orderRepository.findPlacedPendingOrdersAfter(eq(lastTradedTime), eq(reInitedOrderBookId), eq(100)))
                .thenReturn(createOrders(10, reInitedOrderBookId), null);

        executor.reinitialize();

        sellOrderQuantity = random.nextInt(10);
        buyOrderQuantity = random.nextInt(10);

        sell = new Thread(new SellProducer(executor, sellOrderQuantity, reInitedOrderBookId));
        sell.start();
        buy = new Thread(new BuyProducer(executor, buyOrderQuantity, reInitedOrderBookId));
        buy.start();


        sell.join();
        buy.join();

        while ((count - preCount - 10) < sellOrderQuantity + buyOrderQuantity) {
            TimeUnit.MILLISECONDS.sleep(50);
        }
        assertThat(count, equalTo(sellOrderQuantity + buyOrderQuantity + preCount + 10));

        verify(commandGateway, times(count)).send(anyObject());
    }

    private List<Order> createOrders(int orders, OrderBookId orderBookId) {
        final ArrayList<Order> list = Lists.newArrayListWithCapacity(orders);
        for (int i = 0; i < orders; i++) {
            final Order order = new Order(random.nextBoolean() ? OrderType.BUY : OrderType.SELL);
            order.setOrderBookId(orderBookId);
            order.setPrimaryKey(new OrderId().toString());
            list.add(order);
        }

        return list;
    }

    class SellProducer implements Runnable {
        private QueuedTradeExecutor executor;
        private int times;
        private OrderBookId bookId;

        SellProducer(QueuedTradeExecutor executor, int times, OrderBookId bookId) {
            this.executor = executor;
            this.times = times;
            this.bookId = bookId;
        }

        public void run() {
            try {
                while (times > 0) {
                    times--;
                    Order order = new Order(OrderType.SELL);
                    order.setOrderBookId(bookId);
                    order.setPrimaryKey(new OrderId().toString());
                    executor.execute(order);
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(100));
                }
            } catch (InterruptedException ex) {

            }
        }
    }

    class BuyProducer implements Runnable {
        private QueuedTradeExecutor executor;
        private Random random = new Random();
        private int times;
        private OrderBookId bookId;

        BuyProducer(QueuedTradeExecutor executor, int times, OrderBookId bookId) {
            this.executor = executor;
            this.times = times;
            this.bookId = bookId;
        }

        public void run() {
            try {
                while (times > 0) {
                    times--;
                    Order order = new Order(OrderType.BUY);
                    order.setOrderBookId(bookId);
                    order.setPrimaryKey(new OrderId().toString());
                    executor.execute(order);
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(100));
                }
            } catch (InterruptedException ex) {

            }
        }
    }

    private List<OrderBookEntry> createOrderBooks(OrderBookId orderBookId) {
        List<OrderBookEntry> orderBookEntries = Lists.newArrayList();

        for (int i = 0; i < 1; i++) {
            OrderBookEntry orderBookEntry = new OrderBookEntry();
            orderBookEntry.setPrimaryKey(new OrderBookId().toString());
            orderBookEntries.add(orderBookEntry);
        }
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setPrimaryKey(orderBookId.toString());
        orderBookEntries.add(orderBookEntry);
        return orderBookEntries;
    }
}