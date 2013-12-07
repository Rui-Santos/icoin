package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.order.ExecuteSellOrderCommand;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.homhon.mongo.TimeUtils.currentTime;
import static com.homhon.util.Collections.isEmpty;
import static com.homhon.util.Objects.nullSafe;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SellOrderExecutor {
    private static Logger logger = LoggerFactory.getLogger(SellOrderExecutor.class);
    private BuyOrderRepository buyOrderRepository;
    private SellOrderRepository sellOrderRepository;

    private Repository<OrderBook> orderBookRepository;

    @SuppressWarnings("unused")
    @CommandHandler
    public void executeSellOrder(ExecuteSellOrderCommand sellCommand) {
        if (sellCommand == null) {
            return;
        }

        executeSelling(sellCommand);
    }

    private void executeSelling(ExecuteSellOrderCommand sellCommand) {
        OrderBook orderBook = orderBookRepository.load(sellCommand.getOrderBookId());
        //selling price <= the current selling price
        if (orderBook.getLowestSellPrice().compareTo(sellCommand.getItemPrice()) <= 0) {
            return;
        }

        //refresh current buy price
        orderBook.resetLowestSellPrice(sellCommand.getOrderId().toString(), sellCommand.getItemPrice());

        //highest buying price < selling price
//        if (orderBook.getHighestBuyPrice().compareTo(command.getItemPrice()) < 0) {
//            return;
//        }

        //selling price <= than the current highest buying price
        logger.info("Executing selling order {}", sellCommand);

        boolean done = true;
        do {
            final List<BuyOrder> buyOrders =
                    buyOrderRepository.findDescPendingOrdersByPriceTime(
                            sellCommand.getPlaceDate(),
                            sellCommand.getItemPrice(),
                            sellCommand.getOrderBookId(),
                            100);

            logger.info("Executable buy orders with size {} : {}",
                    nullSafe(buyOrders, Collections.<BuyOrder>emptyList()).size(),
                    buyOrders);
            //no selling order matched
            if (isEmpty(buyOrders)) {
                break;
            }

            for (BuyOrder buyOrder : buyOrders) {
                //should not happen here, coz the repo does not return the right result
                if (buyOrder.getItemPrice().compareTo(sellCommand.getItemPrice()) < 0) {
                    logger.warn("Strange here, why buy orders from repo have price less than current selling price!");
                    break;
                }

                final SellOrder sellOrder = sellOrderRepository.findOne(sellCommand.getOrderId().toString());

                BigDecimal matchedTradePrice = buyOrder.getItemPrice();
                BigDecimal matchedTradeAmount = buyOrder.getItemRemaining().min(sellOrder.getItemRemaining());

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing orders with amount {}, price {}: highest buying order {}, lowest selling order {}",
                            matchedTradeAmount, matchedTradePrice, buyOrder, sellCommand);
                    orderBook.executeSelling(matchedTradeAmount,
                            matchedTradePrice,
                            buyOrder.getPrimaryKey(),
                            sellCommand.getOrderId().toString(),
                            buyOrder.getTransactionId(),
                            sellCommand.getTransactionId(),
                            currentTime());
                }

                buyOrder.recordTraded(matchedTradeAmount, currentTime());
                sellOrder.recordTraded(matchedTradeAmount, currentTime());

                buyOrderRepository.save(buyOrder);
                sellOrderRepository.save(sellOrder);

                if (Constants.IGNORED_PRICE.compareTo(sellOrder.getItemRemaining()) >= 0) {
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