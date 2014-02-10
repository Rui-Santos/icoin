package com.icoin.trading.tradeengine.application.command.order.handler;

import com.icoin.trading.tradeengine.application.command.order.ExecuteSellOrderCommand;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static com.homhon.util.Collections.isEmpty;
import static com.homhon.util.Objects.nullSafe;
import static com.icoin.trading.tradeengine.MoneyUtils.convertTo;
import static org.joda.money.MoneyUtils.min;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 1:12 PM
 * Execute sell order.
 */
@Component
public class SellOrderExecutor {
    private static Logger logger = LoggerFactory.getLogger(SellOrderExecutor.class);
    private Repository<OrderBook> orderBookRepository;

    private OrderExecutorHelper orderExecutorHelper;

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
            final List<Order> buyOrders =
                    orderExecutorHelper.findDescPendingOrdersByPriceTime(
                            sellCommand.getPlaceDate(),
                            sellCommand.getItemPrice(),
                            sellCommand.getOrderBookId(),
                            100);

            logger.info("Executable buy orders with size {} : {}",
                    nullSafe(buyOrders, Collections.<BuyOrder>emptyList()).size(),
                    buyOrders);
            //no selling order matched
            if (isEmpty(buyOrders)) {
                return;
            }

            for (Order buyOrder : buyOrders) {
                //should not happen here, coz the repo does not return the right result
                if (buyOrder.getItemPrice().isLessThan(sellCommand.getItemPrice())) {
                    logger.warn("Strange here, why buy orders from repo have price less than current selling price!");
                    break;
                }

                final Order sellOrder = orderExecutorHelper.findSellOrder(sellCommand.getOrderId());

                BigMoney matchedTradePrice = buyOrder.getItemPrice();
                BigMoney matchedTradeAmount = min(buyOrder.getItemRemaining(), sellOrder.getItemRemaining());

                final BigMoney executedMoney = convertTo(matchedTradeAmount, matchedTradePrice).toBigMoney();


                BigMoney buyCommission = orderExecutorHelper.calcExecutedBuyCommission(buyOrder, matchedTradePrice, matchedTradeAmount);
                BigMoney sellCommission = orderExecutorHelper.calcExecutedSellCommission(sellOrder, matchedTradePrice, matchedTradeAmount);

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing orders with amount {}, price {}, buy commission {}, sell commission {}, total money {}: highest buying order {}, lowest selling order {}",
                            matchedTradeAmount, matchedTradePrice, buyCommission, sellCommission, executedMoney, buyOrder, sellCommand);
                    orderBook.executeSelling(matchedTradeAmount,
                            matchedTradePrice,
                            executedMoney,
                            buyOrder.getPrimaryKey(),
                            sellOrder.getPrimaryKey(),
                            buyCommission,
                            sellCommission,
                            buyOrder.getTransactionId(),
                            sellCommand.getTransactionId(),
                            sellCommand.getPlaceDate());
                }

                orderExecutorHelper.recordTraded(
                        buyOrder,
                        sellOrder,
                        buyCommission,
                        sellCommission,
                        matchedTradeAmount,
                        matchedTradePrice,
                        sellCommand.getPlaceDate());

                if (sellOrder.getItemRemaining().isNegativeOrZero()) {
                    done = true;
                    break;
                }
            }
        } while (!done);

        orderExecutorHelper.refresh(orderBook);
    }


    @Autowired
    public void setOrderExecutorHelper(OrderExecutorHelper orderExecutorHelper) {
        this.orderExecutorHelper = orderExecutorHelper;
    }

    @Resource(name = "orderBookRepository")
    public void setOrderBookRepository(Repository<OrderBook> orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }
}