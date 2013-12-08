package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.homhon.mongo.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-8
 * Time: PM10:10
 * Execute helper for order execution.
 * todo, if have time, change it to singleton with repo's set
 */


public abstract class OrderExecutorHelper {
    private static Logger logger = LoggerFactory.getLogger(OrderExecutorHelper.class);

    public static void refresh(OrderBook orderBook,
                               SellOrderRepository sellOrderRepository,
                               BuyOrderRepository buyOrderRepository) {
        final SellOrder lowestSell = sellOrderRepository.findLowestPricePendingOrder(orderBook.getOrderBookId());
        final BuyOrder highestBuy = buyOrderRepository.findHighestPricePendingOrder(orderBook.getOrderBookId());

        if (lowestSell != null) {
            logger.info("Refreshing lowest sell order {} with price {}", lowestSell.getPrimaryKey(), lowestSell.getItemPrice());
            orderBook.resetLowestSellPrice(lowestSell.getPrimaryKey(), lowestSell.getItemPrice());
        }

        if (highestBuy != null) {
            logger.info("Refreshing highest buy order {} with price {}", highestBuy.getPrimaryKey(), highestBuy.getItemPrice());
            orderBook.resetHighestBuyPrice(highestBuy.getPrimaryKey(), highestBuy.getItemPrice());
        }
    }

    public static void recordTraded(BuyOrder buyOrder,
                                    SellOrder sellOrder,
                                    BigDecimal matchedTradeAmount,
                                    SellOrderRepository sellOrderRepository,
                                    BuyOrderRepository buyOrderRepository){
        buyOrder.recordTraded(matchedTradeAmount, currentTime());
        sellOrder.recordTraded(matchedTradeAmount, currentTime());

        buyOrderRepository.save(buyOrder);
        sellOrderRepository.save(sellOrder);
    }
}
