package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class SellOrderExecutor implements OrderExecutor<SellOrder> {
    private static Logger logger = LoggerFactory.getLogger(SellOrderExecutor.class);
    private BuyOrderRepository buyOrderRepository;
    private SellOrderRepository sellOrderRepository;

    private Repository<OrderBook> orderBookRepository;

    @Override
    public void execute(SellOrder element) {
        if (element == null) {
            return;
        }

        executeSelling(element);
    }

    private void executeSelling(SellOrder order) {
        OrderBook orderBook = orderBookRepository.load(order.getOrderBookId());
        //selling price <= the current selling price
        if (orderBook.getLowestSellPrice().compareTo(order.getItemPrice()) <= 0) {
            return;
        }

        //refresh current buy price
        orderBook.resetLowestSellPrice(order);

        //highest buying price < selling price
        if (orderBook.getHighestBuyPrice().compareTo(order.getItemPrice()) < 0) {
            return;
        }

        //selling price <= than the current highest buying price
        logger.info("Executing selling order {}", order);

        boolean done = true;
        do {
            final List<BuyOrder> buyOrders =
                    buyOrderRepository.findDescPendingOrdersByPriceTime(
                            order.getPlaceDate(),
                            order.getItemPrice(),
                            order.getOrderBookId(),
                            100);

            //no selling order matched
            if (isEmpty(buyOrders)) {
                break;
            }

            for (BuyOrder buyOrder : buyOrders) {
                //should not happen here, coz the repo does not return the right result
                if (buyOrder.getItemPrice().compareTo(order.getItemPrice()) < 0) {
                    logger.warn("Strange here, why buy orders from repo have price less than current selling price!");
                    break;
                }

                BigDecimal matchedTradePrice = buyOrder.getItemPrice();
                BigDecimal matchedTradeAmount = buyOrder.getItemsRemaining().min(order.getItemsRemaining());

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing orders with amount {}, price {}: highest buying order {}, lowest selling order {}",
                            matchedTradeAmount, matchedTradePrice, buyOrder, order);
                    orderBook.executeSelling(matchedTradeAmount,
                            matchedTradePrice,
                            buyOrder.getPrimaryKey(),
                            order.getPrimaryKey(),
                            buyOrder.getTransactionId(),
                            order.getTransactionId(),
                            currentTime());
                }

                buyOrder.recordTraded(matchedTradeAmount, currentTime());
                order.recordTraded(matchedTradeAmount, currentTime());

                buyOrderRepository.save(buyOrder);
                sellOrderRepository.save(order);

                if (Constants.IGNORED_PRICE.compareTo(order.getItemsRemaining()) >= 0) {
                    done = true;
                    break;
                }
            }
        } while (!done);

    }

    @Autowired
    public void setBuyOrderRepository(BuyOrderRepository buyOrderRepository) {
        this.buyOrderRepository = buyOrderRepository;
    }

    @Autowired
    public void setSellOrderRepository(SellOrderRepository sellOrderRepository) {
        this.sellOrderRepository = sellOrderRepository;
    }

    @Resource(name = "orderBookRepository")
    public void setOrderBookRepository(Repository<OrderBook> orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }
}