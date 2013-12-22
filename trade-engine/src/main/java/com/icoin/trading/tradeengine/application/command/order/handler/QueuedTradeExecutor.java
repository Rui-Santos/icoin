package com.icoin.trading.tradeengine.application.command.order.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.icoin.trading.tradeengine.application.command.order.ExecuteBuyOrderCommand;
import com.icoin.trading.tradeengine.application.command.order.ExecuteSellOrderCommand;
import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-3
 * Time: AM7:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class QueuedTradeExecutor implements TradeExecutor {
    private static Logger logger = LoggerFactory.getLogger(QueuedTradeExecutor.class);
    private Map<OrderBookId, BlockingQueue<AbstractOrder>> orderBookPool;
    private CommandGateway commandGateway;

    //todo, load orderbook id from the repo
    //todo cannot reloaded runtime
//    QueuedTradeExecutor(Map<OrderBookId, BlockingQueue<AbstractOrder>> orderBookPool) {
//        this.orderBookPool = ImmutableMap.copyOf(orderBookPool);
//    }

    @Autowired
    public QueuedTradeExecutor(OrderBookQueryRepository orderBookRepository, CommandGateway commandGateway) {
        this.commandGateway = commandGateway;

        initOrderBookPool(orderBookRepository);
        start();
    }

    private void initOrderBookPool(OrderBookQueryRepository orderBookRepository) {
        final Iterable<OrderBookEntry> orderBookEntries = orderBookRepository.findAll();
        final HashMap<OrderBookId, BlockingQueue<AbstractOrder>> map = Maps.newHashMap();

        for (OrderBookEntry orderBook : orderBookEntries) {
            map.put(new OrderBookId(orderBook.getPrimaryKey()),
                    new LinkedBlockingDeque<AbstractOrder>());
            logger.warn("initialized order book trading pool with {}", orderBook);
        }
        this.orderBookPool = ImmutableMap.copyOf(map);
    }

    @Override
    public <T extends AbstractOrder> void execute(T element) {
        final OrderBookId orderBookId = element.getOrderBookId();

        if (!orderBookPool.containsKey(orderBookId)) {
            logger.warn("order book id is {}, not in the pool {}", orderBookId, orderBookPool.keySet());
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
        private boolean stop;

        TradeExecutor(OrderBookId orderBookId, BlockingQueue<AbstractOrder> q) {
            this.queue = q;
            this.orderBookId = orderBookId;
        }

        public void run() {
            try {
                while (!stop) {
                    consume(queue.take());
                }
            } catch (InterruptedException ex) {
                logger.warn("Interruppted Queue for orderbookId {} when De-queuing", orderBookId);
            }
        }

        public void stop(){
            stop = true;
            queue.clear();
        }

        void consume(AbstractOrder order) {
            logger.info("Excuting order {}:{}", order.getOrderType(), order);
            switch (order.getOrderType()) {
                case BUY:
                    final ExecuteBuyOrderCommand executeBuyOrderCommand =
                            new ExecuteBuyOrderCommand(new OrderId(order.getPrimaryKey()),
                                    order.getPortfolioId(),
                                    order.getOrderBookId(),
                                    order.getTransactionId(),
                                    order.getItemRemaining(),
                                    order.getItemPrice(),
                                    order.getPlaceDate());
                    commandGateway.send(executeBuyOrderCommand);
                    break;
                case SELL:
                    final ExecuteSellOrderCommand executeSellOrderCommand =
                            new ExecuteSellOrderCommand(new OrderId(order.getPrimaryKey()),
                                    order.getPortfolioId(),
                                    order.getOrderBookId(),
                                    order.getTransactionId(),
                                    order.getItemRemaining(),
                                    order.getItemPrice(),
                                    order.getPlaceDate());
                    commandGateway.send(executeSellOrderCommand);
                    break;
                default:
                    throw new UnsupportedOperationException("order type not supported for executing, type:" + order.getOrderType());
            }
        }
    }

    class Setup implements Runnable {
        final ExecutorService executor;

        private Setup(){
            executor =  Executors.newFixedThreadPool(orderBookPool.size());
        }

        @Override
        public void run() {
//             ExecutorService executor = Executors.newFixedThreadPool(orderBookPool.size());
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
}
