package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static com.homhon.mongo.TimeUtils.currentTime;
import static com.homhon.util.Collections.isEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
//todo put them int executor
public class BuyOrderExecutor implements OrderExecutor<BuyOrder> {
    private static Logger logger = LoggerFactory.getLogger(BuyOrderExecutor.class);
    private SellOrderRepository sellOrderRepository;
    private BuyOrderRepository buyOrderRepository;

    private Repository<OrderBook> orderBookRepository;

    @Override
    public void execute(BuyOrder element) {
        if (element == null) {
            return;
        }

        executeBuying(element);
    }

    private void executeBuying(BuyOrder order) {
        OrderBook orderBook = orderBookRepository.load(order.getOrderBookId());

        //buying price is less than the current buying price
        if (orderBook.getHighestBuyPrice().compareTo(order.getItemPrice()) >= 0) {
            return;
        }

        //refresh current buy price
        orderBook.resetHighestBuyPrice(order);

        //lowest sell > the current buying price
        if (orderBook.getLowestSellPrice().compareTo(order.getItemPrice()) > 0) {
            return;
        }

        //buying price >= than the current highest selling  price
        logger.info("Executing Buying order {}", order);

        boolean done = true;
        do {
            final List<SellOrder> sellOrders =
                    sellOrderRepository.findAscPendingOrdersByPriceTime(
                            order.getPlaceDate(),
                            order.getItemPrice(),
                            order.getOrderBookId(),
                            100);

            //no selling order matched
            if (isEmpty(sellOrders)) {
                break;
            }

            for (SellOrder sellOrder : sellOrders) {
                //should not happen here, coz the repo does not return the right result
                if (sellOrder.getItemPrice().compareTo(order.getItemPrice()) > 0) {
                    logger.warn("Strange here, why sell orders from repo have price greater than current buy price!");
                    break;
                }

                BigDecimal matchedTradePrice = sellOrder.getItemPrice();
                BigDecimal matchedTradeAmount = sellOrder.getItemsRemaining().min(order.getItemsRemaining());

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing orders with amount {}, price {}: highest buying order {}, lowest selling order {}",
                            matchedTradeAmount, matchedTradePrice, sellOrder, order);
                    orderBook.executeBuying(matchedTradeAmount,
                            matchedTradePrice,
                            sellOrder.getPrimaryKey(),
                            order.getPrimaryKey(),
                            sellOrder.getTransactionId(),
                            order.getTransactionId());
                }

                sellOrder.recordTraded(matchedTradeAmount,currentTime());
                order.recordTraded(matchedTradeAmount,currentTime());

                sellOrderRepository.save(sellOrder);
                buyOrderRepository.save(order);

                if (BigDecimal.ZERO.compareTo(order.getItemsRemaining()) >= 0) {
                    done = true;
                    break;
                }
            }
        } while (!done);
    }

    @Autowired
    public SellOrderRepository getSellOrderRepository() {
        return sellOrderRepository;
    }

    @Autowired
    public void setBuyOrderRepository(BuyOrderRepository buyOrderRepository) {
        this.buyOrderRepository = buyOrderRepository;
    }

    @Resource(name = "orderBookRepository")
    public void setOrderBookRepository(Repository<OrderBook> orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }
}