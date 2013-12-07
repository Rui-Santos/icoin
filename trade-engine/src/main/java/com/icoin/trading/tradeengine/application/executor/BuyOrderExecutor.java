package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.order.ExecuteBuyOrderCommand;
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
public class BuyOrderExecutor {
    private static Logger logger = LoggerFactory.getLogger(BuyOrderExecutor.class);
    private SellOrderRepository sellOrderRepository;
    private BuyOrderRepository buyOrderRepository;

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
        orderBook.resetHighestBuyPrice(buyCommand.getOrderId().toString(),buyCommand.getItemPrice());

        //lowest sell > the current buying price
//        if (orderBook.getLowestSellPrice().compareTo(buyCommand.getItemPrice()) > 0) {
//            return;
//        }

        //buying price >= than the current highest selling  price
        logger.info("Executing Buying order {}", buyCommand);

        boolean done = true;
        do {
            final List<SellOrder> sellOrders =
                    sellOrderRepository.findAscPendingOrdersByPriceTime(
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

                final BuyOrder buyOrder = buyOrderRepository.findOne(buyCommand.getOrderId().toString());

                BigDecimal matchedTradePrice = sellOrder.getItemPrice();
                BigDecimal matchedTradeAmount = sellOrder.getItemRemaining().min(buyOrder.getItemRemaining());

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing orders with amount {}, price {}: highest buying order {}, lowest selling order {}",
                            matchedTradeAmount, matchedTradePrice, sellOrder, buyCommand);
                    orderBook.executeBuying(matchedTradeAmount,
                            matchedTradePrice,
                            sellOrder.getPrimaryKey(),
                            buyOrder.getPrimaryKey(),
                            sellOrder.getTransactionId(),
                            buyCommand.getTransactionId(),
                            currentTime());
                }

                sellOrder.recordTraded(matchedTradeAmount, currentTime());
                buyOrder.recordTraded(matchedTradeAmount, currentTime());

                sellOrderRepository.save(sellOrder);
                buyOrderRepository.save(buyOrder);

                if (Constants.IGNORED_PRICE.compareTo(buyOrder.getItemRemaining()) >= 0) {
                    done = true;
                    break;
                }
            }
        } while (!done);
    }

    @Autowired
    public void setSellOrderRepository(SellOrderRepository sellOrderRepository) {
        this.sellOrderRepository = sellOrderRepository;
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