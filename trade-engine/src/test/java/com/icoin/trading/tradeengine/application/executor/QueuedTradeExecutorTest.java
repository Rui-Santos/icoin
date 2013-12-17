package com.icoin.trading.tradeengine.application.executor;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyObject;
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
    private CommandGateway commandGateway = mock(CommandGateway.class);
    private OrderBookQueryRepository orderBookQueryRepository = mock(OrderBookQueryRepository.class);
    private QueuedTradeExecutor executor;
    private final Random random = new Random();
    private int count = 0;

    @Before
    public void setUp() throws Exception {
        when(orderBookQueryRepository.findAll()).thenReturn(createOrderBooks());

        executor = new QueuedTradeExecutor(orderBookQueryRepository, commandGateway);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TimeUnit.MILLISECONDS.sleep(10 + random.nextInt(500));
                count++;
                return null;
            }
        }).when(commandGateway).send(anyObject());
    }

    private List<OrderBookEntry> createOrderBooks() {
        List<OrderBookEntry> orderBookEntries = Lists.newArrayList();

        for (int i = 0; i < 20; i++) {
            OrderBookEntry orderBookEntry = new OrderBookEntry();
            orderBookEntry.setPrimaryKey(orderBookId.toString());
            orderBookEntries.add(orderBookEntry);
        }
        return orderBookEntries;
    }

    @Test
    public void testExecution() throws Exception {
        executor.start();

        int sellOrderQuantity = random.nextInt(10);
        int buyOrderQuantity = random.nextInt(10);

        Thread sell = new Thread(new SellProducer(executor, sellOrderQuantity));
        sell.start();
        Thread buy = new Thread(new BuyProducer(executor, buyOrderQuantity));
        buy.start();


        sell.join();
        buy.join();

        TimeUnit.SECONDS.sleep(2);
        assertThat(count, equalTo(sellOrderQuantity + buyOrderQuantity));

        verify(commandGateway, times(count)).send(anyObject());
    }

    class SellProducer implements Runnable {
        private QueuedTradeExecutor executor;
        private int times;

        SellProducer(QueuedTradeExecutor executor, int times) {
            this.executor = executor;
            this.times = times;
        }

        public void run() {
            try {
                while (times > 0) {
                    times--;
                    SellOrder order = new SellOrder();
                    order.setOrderBookId(orderBookId);
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

        BuyProducer(QueuedTradeExecutor executor, int times) {
            this.executor = executor;
            this.times = times;
        }

        public void run() {
            try {
                while (times > 0) {
                    times--;
                    BuyOrder order = new BuyOrder();
                    order.setOrderBookId(orderBookId);
                    order.setPrimaryKey(new OrderId().toString());
                    executor.execute(order);
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(100));
                }
            } catch (InterruptedException ex) {

            }
        }
    }
}