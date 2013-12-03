package com.icoin.trading.tradeengine.application.command.order;

import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.homhon.util.Collections.isEmpty;

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
    private SellOrderRepository sellOrderRepository;
    private BuyOrderRepository buyOrderRepository;

    public QueuedTradeExecutor() {
    }

    @Override
    public <T extends AbstractOrder> void put(T element) {
        final OrderBookId orderBookId = element.getOrderBookId();

        if (!orderBookPool.containsKey(orderBookId)) {
            return;
        }

        final BlockingQueue<AbstractOrder> orderQueue = orderBookPool.get(orderBookId);

        try {
            orderQueue.put(element);
        } catch (InterruptedException e) {
            logger.warn("Interruppted Queue for orderbookId {} when En-queuing", orderBookId);
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
                    executeBuying(order);
                    break;
                case SELL:

                    break;
            }
        }
    }

    private void executeBuying(AbstractOrder order) {

        //compare with orderbook repository to see if it's the best price to take
        boolean done = true;
        do {
            final List<SellOrder> sellOrders =
                    sellOrderRepository.findOrderedPendingOrdersByPriceTime(
                            order.getPlaceDate(),
                            order.getItemPrice(),
                            order.getOrderBookId(),
                            100);

            //no selling order matched
            if(isEmpty(sellOrders)){
              return;
            }

            order.getItemPrice();

            for (SellOrder sellOrder : sellOrders) {

            }

        } while (!done);

    }

    private void executeTrades() {
        boolean tradingDone = false;
        while (!tradingDone && !buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            BuyOrder highestBuyer = buyOrders.last();
            SellOrder lowestSeller = sellOrders.first();
            if (highestBuyer.getItemPrice().compareTo(lowestSeller.getItemPrice()) >= 0) {
                //highestBuyer.price >= lowestSeller.price
                BigDecimal matchedTradeAmount = highestBuyer.getItemsRemaining().min(lowestSeller.getItemsRemaining());

                //todo price method
                BigDecimal matchedTradePrice = (highestBuyer.getItemPrice().add(lowestSeller.getItemPrice()).divide(
                        BigDecimal.valueOf(2), 2, RoundingMode.HALF_EVEN));

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing orders with amount {}, price {}: highest buying order {}, lowest selling order {}",
                            matchedTradeAmount, matchedTradePrice, highestBuyer, lowestSeller);
                }

                apply(new TradeExecutedEvent(orderBookId,
                        matchedTradeAmount,
                        matchedTradePrice,
                        highestBuyer.getPrimaryKey(),
                        lowestSeller.getPrimaryKey(),
                        highestBuyer.getTransactionId(),
                        lowestSeller.getTransactionId()));
            } else {
                tradingDone = true;
            }
        }


//        if(buyOrders.size() <= MAX_THREHOLD){
//            apply(new RefreshingBuyOrdersEvent());
//        }
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
}
