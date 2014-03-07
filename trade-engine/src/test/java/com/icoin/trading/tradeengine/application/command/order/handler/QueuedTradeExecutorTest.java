package com.icoin.trading.tradeengine.application.command.order.handler;


import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.OrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import com.icoin.trading.tradeengine.domain.model.admin.TradingSystemStatus;
import com.icoin.trading.tradeengine.domain.service.TradingSystemService;
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

import static com.homhon.util.TimeUtils.currentTime;
import static com.homhon.util.TimeUtils.futureMinute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
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
 * Date: 3/6/14
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueuedTradeExecutorTest {
    private final OrderBookId orderBookId = new OrderBookId();
    private final OrderBookId reInitedOrderBookId = new OrderBookId();
    private CommandGateway commandGateway = mock(CommandGateway.class);
    private OrderBookQueryRepository orderBookQueryRepository = mock(OrderBookQueryRepository.class);
    private TradingSystemService tradingSystemService = mock(TradingSystemService.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private final Random random = new Random();
    private volatile int count = 0;
    private TradingSystemStatus goodTradingStatus = new TradingSystemStatus();

    @Before
    public void setUp() throws Exception {
        doAnswer(new Answer() {
            @Override
            public synchronized Object answer(InvocationOnMock invocation) throws Throwable {
                TimeUnit.MILLISECONDS.sleep(10 + random.nextInt(20));
                count++;
                return null;
            }
        }).when(commandGateway).send(anyObject());
    }

    @Test(timeout = 30000L)
    public void testExecution() throws Exception {
        when(tradingSystemService.currentStatus()).thenReturn(goodTradingStatus);
        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks(orderBookId));
        QueuedTradeExecutor executor =
                new QueuedTradeExecutor(orderBookQueryRepository,
                        commandGateway,
                        orderRepository,
                        tradingSystemService);

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
        verify(tradingSystemService, times(count)).currentStatus();
    }

    @Test(timeout = 30000L)
    public void testExecutionWithOrderInitialization() throws Exception {
        final Order highestBuy = new Order(OrderType.BUY);
        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.EUR, 10);
        highestBuy.setItemPrice(highestBuyPrice);

        when(tradingSystemService.currentStatus()).thenReturn(goodTradingStatus);
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
        QueuedTradeExecutor executor = new QueuedTradeExecutor(orderBookQueryRepository,
                commandGateway,
                orderRepository,
                tradingSystemService);

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
        verify(tradingSystemService, times(count)).currentStatus();
    }

    @Test(timeout = 60000L)
    public void testReinitialize() throws Exception {
        when(tradingSystemService.currentStatus()).thenReturn(goodTradingStatus);
        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks(orderBookId),
                createOrderBooks(reInitedOrderBookId));
        QueuedTradeExecutor executor =
                new QueuedTradeExecutor(
                        orderBookQueryRepository,
                        commandGateway,
                        orderRepository,
                        tradingSystemService);
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
        verify(tradingSystemService, times(count)).currentStatus();
    }

    @Test(timeout = 60000L)
    public void testHaltWithoutResolvingNotExecuted() throws Exception {
        final int sellOrderQuantity = random.nextInt(10);
        final int buyOrderQuantity = random.nextInt(10);
        final int executed = (sellOrderQuantity + buyOrderQuantity) / 2;

        TradingSystemStatus status = mock(TradingSystemStatus.class);

        when(status.allowedToTrade(any(Date.class))).thenAnswer(new Answer<Boolean>() {
            private int count;

            @Override
            public synchronized Boolean answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count > executed;
            }
        });

        when(tradingSystemService.currentStatus()).thenReturn(status);
        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks(orderBookId),
                createOrderBooks(reInitedOrderBookId));
        QueuedTradeExecutor executor =
                new QueuedTradeExecutor(
                        orderBookQueryRepository,
                        commandGateway,
                        orderRepository,
                        tradingSystemService);
        executor.start();

        Thread sell = new Thread(new SellProducer(executor, sellOrderQuantity, orderBookId));
        sell.start();
        Thread buy = new Thread(new BuyProducer(executor, buyOrderQuantity, orderBookId));
        buy.start();

        sell.join();
        buy.join();

        while (count < (sellOrderQuantity + buyOrderQuantity - executed)) {
            TimeUnit.MILLISECONDS.sleep(50);
        }

        assertThat(count, equalTo(sellOrderQuantity + buyOrderQuantity - executed));

        verify(commandGateway, times(count)).send(anyObject());
        verify(tradingSystemService, times(sellOrderQuantity + buyOrderQuantity)).currentStatus();
    }


    @Test(timeout = 60000L)
    public void testHaltWithResolvingNotExecuted() throws Exception {
        final int sellOrderQuantity = random.nextInt(10);
        final int buyOrderQuantity = random.nextInt(10);
        final int executed = (sellOrderQuantity + buyOrderQuantity) / 2;

        TradingSystemStatus status = mock(TradingSystemStatus.class);

        when(status.allowedToTrade(any(Date.class))).thenAnswer(new Answer<Boolean>() {
            private int count;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count > executed;
            }
        });


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

        OrderBookEntry orderbook = new OrderBookEntry();
        final Date lastTradedTime = futureMinute(currentTime(), 10);
        orderbook.setLastTradedTime(lastTradedTime);
        orderbook.setPrimaryKey(orderBookId.toString());
        when(orderBookQueryRepository.findOne(eq(orderBookId.toString())))
                .thenReturn(orderbook);

        when(orderRepository.findPlacedPendingOrdersAfter(eq(lastTradedTime), eq(orderBookId), eq(100)))
                .thenReturn(null, createOrders(100, orderBookId), createOrders(5, orderBookId), null);


        when(tradingSystemService.currentStatus()).thenReturn(status);
        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks(orderBookId),
                createOrderBooks(reInitedOrderBookId));
        QueuedTradeExecutor executor =
                new QueuedTradeExecutor(
                        orderBookQueryRepository,
                        commandGateway,
                        orderRepository,
                        tradingSystemService);
        executor.start();

        Thread sell = new Thread(new SellProducer(executor, sellOrderQuantity, orderBookId));
        sell.start();
        Thread buy = new Thread(new BuyProducer(executor, buyOrderQuantity, orderBookId));
        buy.start();

        sell.join();
        buy.join();

        while (count < (sellOrderQuantity + buyOrderQuantity - executed + 105)) {
            System.out.println(count + ", need:: " + (sellOrderQuantity + buyOrderQuantity - executed + 105));
            TimeUnit.MILLISECONDS.sleep(50);
        }

        assertThat(count, equalTo(sellOrderQuantity + buyOrderQuantity - executed + 105));

        verify(commandGateway, times(count)).send(anyObject());
        verify(tradingSystemService, times(sellOrderQuantity + buyOrderQuantity + 105)).currentStatus();
    }

    @Test(timeout = 60000L)
    public void testHaltWithResolvingNotExecutedWhenInit() throws Exception {
        final int sellOrderQuantity = random.nextInt(10);
        final int buyOrderQuantity = random.nextInt(10);
        final int executed = (105 + sellOrderQuantity + buyOrderQuantity) / 2;

        TradingSystemStatus status = mock(TradingSystemStatus.class);

        when(status.allowedToTrade(any(Date.class))).thenAnswer(new Answer<Boolean>() {
            private int count;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count > executed;
            }
        });


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
                .thenReturn(createOrders(100, orderBookId), createOrders(1, orderBookId),createOrders(4, orderBookId), null);


        when(tradingSystemService.currentStatus()).thenReturn(status);
        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks(orderBookId),
                createOrderBooks(reInitedOrderBookId));
        QueuedTradeExecutor executor =
                new QueuedTradeExecutor(
                        orderBookQueryRepository,
                        commandGateway,
                        orderRepository,
                        tradingSystemService);
        executor.start();

        Thread sell = new Thread(new SellProducer(executor, sellOrderQuantity, orderBookId));
        sell.start();
        Thread buy = new Thread(new BuyProducer(executor, buyOrderQuantity, orderBookId));
        buy.start();

        sell.join();
        buy.join();

        while (count < (sellOrderQuantity + buyOrderQuantity - executed + 105)) {
            TimeUnit.MILLISECONDS.sleep(50);
        }

        assertThat(count, equalTo(sellOrderQuantity + buyOrderQuantity - executed + 105));

        verify(commandGateway, times(count)).send(anyObject());
        verify(tradingSystemService, times(sellOrderQuantity + buyOrderQuantity + 105)).currentStatus();
    }

    private List<Order> createOrders(int orders, OrderBookId orderBookId) {
        final ArrayList<Order> list = Lists.newArrayListWithCapacity(orders);
        for (int i = 0; i < orders; i++) {
            final Order order = new Order(random.nextBoolean() ? OrderType.BUY : OrderType.SELL);
            order.setOrderBookId(orderBookId);
            order.setPrimaryKey(new OrderId().toString());
            order.setPlaceDate(new Date());
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
                    order.setPlaceDate(new Date());
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
                    order.setPlaceDate(new Date());
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