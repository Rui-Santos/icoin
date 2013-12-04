package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.domain.model.order.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class QueuedTradeExecutorIT {
    private SellOrderRepository sellOrderRepository;
    private BuyOrderRepository buyOrderRepository;
    private QueuedTradeExecutor executor;
    private Map<OrderBookId, BlockingQueue<AbstractOrder>> orderBookPool;

    @Before
    public void setUp() throws Exception {
        orderBookPool = createOrderBookPool();
        executor = new QueuedTradeExecutor(orderBookPool);

        final Random random = new Random();
//        executor.setBuyOrderRepository(createBuyOrderRepository(random));
//        executor.setSellOrderRepository(createSellOrderRepository(random));
    }

    private SellOrderRepository createSellOrderRepository(final Random random) {
        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
                any(Date.class),
                any(BigDecimal.class),
                any(OrderBookId.class),
                any(Integer.class)
        )).thenAnswer(new Answer<List<SellOrder>>() {
            @Override
            public List<SellOrder> answer(InvocationOnMock invocation) throws Throwable {
                TimeUnit.MILLISECONDS.sleep(100 + random.nextInt(1000));
                return null;
            }
        });

        return sellOrderRepository;
    }

    private BuyOrderRepository createBuyOrderRepository(final Random random) {
        when(buyOrderRepository.findDescPendingOrdersByPriceTime(
                any(Date.class),
                any(BigDecimal.class),
                any(OrderBookId.class),
                any(Integer.class)
        )).thenAnswer(new Answer<List<BuyOrder>>() {
            @Override
            public List<BuyOrder> answer(InvocationOnMock invocation) throws Throwable {
                TimeUnit.MILLISECONDS.sleep(100 + random.nextInt(1000));
                return null;
            }
        });

        return buyOrderRepository;
    }

    private Map<OrderBookId, BlockingQueue<AbstractOrder>> createOrderBookPool() {
        Map<OrderBookId, BlockingQueue<AbstractOrder>> orderBookPool =
                new HashMap<OrderBookId, BlockingQueue<AbstractOrder>>();

        for (int i = 0; i < 20; i++) {
            orderBookPool.put(new OrderBookId(), new LinkedBlockingQueue<AbstractOrder>());
        }
        return orderBookPool;
    }

    @Test
    public void testPerformance() {


        executor.start();

        for (OrderBookId orderBookId : orderBookPool.keySet()) {

        }

    }

    class Producer implements Runnable {
        private QueuedTradeExecutor executor;
        Random random = new Random();

        Producer(QueuedTradeExecutor executor) {
            this.executor = executor;
        }

        public void run() {
            try {
                while (true) {
                    executor.execute(new SellOrder());
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(100));
                }
            } catch (InterruptedException ex) {

            }
        }
    }
}