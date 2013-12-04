package com.icoin.trading.tradeengine.application.executor;

import com.google.common.collect.ImmutableMap;
import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-3
 * Time: AM7:47
 * To change this template use File | Settings | File Templates.
 */
public class QueuedTradeExecutor implements TradeExecutor {
    private static Logger logger = LoggerFactory.getLogger(QueuedTradeExecutor.class);
    private Map<OrderBookId, BlockingQueue<AbstractOrder>> orderBookPool;
    private SellOrderExecutor sellOrderExecutor;
    private BuyOrderExecutor buyOrderExecutor;

    //todo, load orderbook id from the repo
    QueuedTradeExecutor(Map<OrderBookId, BlockingQueue<AbstractOrder>> orderBookPool) {
        this.orderBookPool = ImmutableMap.copyOf(orderBookPool);
    }

    @Override
    public <T extends AbstractOrder> void execute(T element) {
        final OrderBookId orderBookId = element.getOrderBookId();

        if (!orderBookPool.containsKey(orderBookId)) {
            return;
        }

        final BlockingQueue<AbstractOrder> orderQueue = orderBookPool.get(orderBookId);

        try {
            orderQueue.put(element);
        } catch (InterruptedException e) {
            logger.warn("Interrupted Queue for orderbookId {} when En-queuing", orderBookId);
        }
    }

    class TradeExecutor implements Runnable {
        private final BlockingQueue<AbstractOrder> queue;
        private final OrderBookId orderBookId;

        TradeExecutor(OrderBookId orderBookId, BlockingQueue<AbstractOrder> q) {
            this.queue = q;
            this.orderBookId = orderBookId;
        }

        public void run() {
            try {
                while (true) {
                    consume(queue.take());
                }
            } catch (InterruptedException ex) {
                logger.warn("Interruppted Queue for orderbookId {} when De-queuing", orderBookId);
            }
        }

        void consume(AbstractOrder order) {
            switch (order.getOrderType()) {
                case BUY:
                    buyOrderExecutor.execute((BuyOrder) order);
                    break;
                case SELL:
                    sellOrderExecutor.execute((SellOrder) order);
                    break;
                default:
                    throw new UnsupportedOperationException("order type not supported for executing, type:" + order.getOrderType());
            }
        }
    }

    class Setup implements Runnable {
        @Override
        public void run() {
            final ExecutorService executor = Executors.newFixedThreadPool(orderBookPool.size());

            for (OrderBookId orderBookId : orderBookPool.keySet()) {
                final BlockingQueue<AbstractOrder> queue = orderBookPool.get(orderBookId);

                executor.execute(
                        new TradeExecutor(orderBookId, queue)
                );
            }
        }
    }

    public void start() {
        final Runnable setup = new Setup();
        new Thread(setup).start();
    }

    @Autowired
    public void setSellOrderExecutor(SellOrderExecutor sellOrderExecutor) {
        this.sellOrderExecutor = sellOrderExecutor;
    }

    @Autowired
    public void setBuyOrderExecutor(BuyOrderExecutor buyOrderExecutor) {
        this.buyOrderExecutor = buyOrderExecutor;
    }
}
