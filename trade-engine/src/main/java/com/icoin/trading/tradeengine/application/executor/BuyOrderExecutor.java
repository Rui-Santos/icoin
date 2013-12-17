package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.application.command.order.ExecuteBuyOrderCommand;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
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
import java.math.RoundingMode;
import java.util.List;

import static com.homhon.util.Collections.isEmpty;
import static org.joda.money.MoneyUtils.min;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 1:12 PM
 * Execute buy order.
 */
@Component
public class BuyOrderExecutor {
    private static Logger logger = LoggerFactory.getLogger(BuyOrderExecutor.class);
    private OrderExecutorHelper orderExecutorHelper;

    private Repository<OrderBook> orderBookRepository;

    @SuppressWarnings("unused")
    @CommandHandler
    public void executeBuyOrder(ExecuteBuyOrderCommand command) {
        if (command == null) {
            return;
        }

        executeBuying(command);
    }

    private void executeBuying(ExecuteBuyOrderCommand buyCommand) {
        OrderBook orderBook = orderBookRepository.load(buyCommand.getOrderBookId());

        //buying price >= current buying price
        if (orderBook.getHighestBuyPrice().compareTo(buyCommand.getItemPrice()) >= 0) {
            return;
        }

        //refresh current buy price
        orderBook.resetHighestBuyPrice(buyCommand.getOrderId().toString(), buyCommand.getItemPrice());

        //buying price >= than the current highest selling  price
        logger.info("Executing Buying order {}", buyCommand);

        boolean done = true;
        do {
            final List<SellOrder> sellOrders =
                    orderExecutorHelper.findAscPendingOrdersByPriceTime(
                            buyCommand.getPlaceDate(),
                            buyCommand.getItemPrice(),
                            buyCommand.getOrderBookId(),
                            100);

            //no selling order matched
            if (isEmpty(sellOrders)) {
                break;
            }

            for (SellOrder sellOrder : sellOrders) {
                //should not happen here, coz the repo does not return the right result
                if (sellOrder.getItemPrice().compareTo(buyCommand.getItemPrice()) > 0) {
                    logger.warn("Strange here, why sell orders from repo have price greater than current buy price!");
                    break;
                }

                final BuyOrder buyOrder = orderExecutorHelper.findBuyOrder(buyCommand.getOrderId());

                BigMoney matchedTradePrice = sellOrder.getItemPrice();
                BigMoney matchedTradeAmount = min(sellOrder.getItemRemaining(), buyOrder.getItemRemaining());

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing orders with amount {}, price {}: highest buying order {}, lowest selling order {}",
                            matchedTradeAmount, matchedTradePrice, sellOrder, buyCommand);
                    orderBook.executeBuying(matchedTradeAmount,
                            matchedTradePrice,
                            buyOrder.getPrimaryKey(),
                            sellOrder.getPrimaryKey(),
                            buyOrder.getTransactionId(),
                            sellOrder.getTransactionId(),
                            buyCommand.getPlaceDate());
                }

                orderExecutorHelper.recordTraded(
                        buyOrder,
                        sellOrder,
                        matchedTradeAmount,
                        buyCommand.getPlaceDate());

                if (buyOrder.getItemRemaining().toMoney(RoundingMode.HALF_EVEN).isNegativeOrZero()) {
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