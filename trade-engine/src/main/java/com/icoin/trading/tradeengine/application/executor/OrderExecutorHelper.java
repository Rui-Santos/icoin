package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-8
 * Time: PM10:10
 * Execute helper for order execution.
 * todo, if have time, change it to singleton with repo's set
 */
@Component
public class OrderExecutorHelper {
    private SellOrderRepository sellOrderRepository;
    private BuyOrderRepository buyOrderRepository;

    private static Logger logger = LoggerFactory.getLogger(OrderExecutorHelper.class);

//    public static void refresh(OrderBook orderBook,
//                               SellOrderRepository sellOrderRepository,
//                               BuyOrderRepository buyOrderRepository) {
//        final SellOrder lowestSell = sellOrderRepository.findLowestPricePendingOrder(orderBook.getOrderBookId());
//        final BuyOrder highestBuy = buyOrderRepository.findHighestPricePendingOrder(orderBook.getOrderBookId());
//
//        if (lowestSell != null) {
//            logger.info("Refreshing lowest sell order {} with price {}", lowestSell.getPrimaryKey(), lowestSell.getItemPrice());
//            orderBook.resetLowestSellPrice(lowestSell.getPrimaryKey(), lowestSell.getItemPrice());
//        }
//
//        if (highestBuy != null) {
//            logger.info("Refreshing highest buy order {} with price {}", highestBuy.getPrimaryKey(), highestBuy.getItemPrice());
//            orderBook.resetHighestBuyPrice(highestBuy.getPrimaryKey(), highestBuy.getItemPrice());
//        }
//    }
//
//    public static void recordTraded(BuyOrder buyOrder,
//                                    SellOrder sellOrder,
//                                    BigDecimal matchedTradeAmount,
//                                    SellOrderRepository sellOrderRepository,
//                                    BuyOrderRepository buyOrderRepository){
//        buyOrder.recordTraded(matchedTradeAmount, currentTime());
//        sellOrder.recordTraded(matchedTradeAmount, currentTime());
//
//        buyOrderRepository.save(buyOrder);
//        sellOrderRepository.save(sellOrder);
//    }

    public List<SellOrder> findAscPendingOrdersByPriceTime(Date toTime,
                                                           BigMoney price,
                                                           OrderBookId orderBookId,
                                                           int size) {
        notNull(toTime);
        notNull(price);
        isTrue(price.isPositive(), "Price should be greater than 0!");
        isTrue(size > 0, "Size should be greater than 0!");


        if (logger.isDebugEnabled()) {
            logger.debug("To find asc sell pending orders with toTime{}, price {}, orderBookId {}, size {} ",
                    toTime, price, orderBookId, size);
        }

        List<SellOrder> list = sellOrderRepository.findAscPendingOrdersByPriceTime(toTime, price, orderBookId, size);

        if (logger.isDebugEnabled()) {
            logger.debug("Found asc sell pending orders with toTime{}, price {}, orderBookId {}, size {}: ",
                    toTime, price, orderBookId, size, list);
        }
        return list;
    }


    public List<BuyOrder> findDescPendingOrdersByPriceTime(Date toTime,
                                                           BigMoney price,
                                                           OrderBookId orderBookId,
                                                           int size) {
        notNull(toTime);
        notNull(price);
        isTrue(price.isPositive(), "Price should be greater than 0!");
        isTrue(size > 0, "Size should be greater than 0!");


        if (logger.isDebugEnabled()) {
            logger.debug("To find desc buy pending orders with toTime{}, price {}, orderBookId {}, size {} ",
                    toTime, price, orderBookId, size);
        }

        List<BuyOrder> list = buyOrderRepository.findDescPendingOrdersByPriceTime(toTime, price, orderBookId, size);

        if (logger.isDebugEnabled()) {
            logger.debug("Found desc buy pending orders with toTime{}, price {}, orderBookId {}, size {}: ",
                    toTime, price, orderBookId, size, list);
        }
        return list;
    }

    public SellOrder findSellOrder(OrderId orderId) {
        notNull(orderId);
        final SellOrder sellOrder = sellOrderRepository.findOne(orderId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("sellOrder {} found: {}", orderId, sellOrder);
        }

        return sellOrder;
    }

    public BuyOrder findBuyOrder(OrderId orderId) {
        notNull(orderId);
        final BuyOrder buyOrder = buyOrderRepository.findOne(orderId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("buyOrder {} found: {}", orderId, buyOrder);
        }

        return buyOrder;
    }

    public void refresh(OrderBook orderBook) {
        notNull(orderBook);

        OrderBookId orderBookId = orderBook.getOrderBookId();
        notNull(orderBook.getOrderBookId());

        final SellOrder lowestSell = sellOrderRepository.findLowestPricePendingOrder(orderBookId);
        final BuyOrder highestBuy = buyOrderRepository.findHighestPricePendingOrder(orderBookId);

        if (lowestSell != null) {
            logger.info("Refreshing lowest sell order {} with price {}", lowestSell.getPrimaryKey(), lowestSell.getItemPrice());
            orderBook.resetLowestSellPrice(lowestSell.getPrimaryKey(), lowestSell.getItemPrice());
        }

        if (highestBuy != null) {
            logger.info("Refreshing highest buy order {} with price {}", highestBuy.getPrimaryKey(), highestBuy.getItemPrice());
            orderBook.resetHighestBuyPrice(highestBuy.getPrimaryKey(), highestBuy.getItemPrice());
        }
    }

    public void recordTraded(BuyOrder buyOrder,
                             SellOrder sellOrder,
                             BigMoney matchedTradeAmount,
                             Date tradedDate) {
        notNull(buyOrder);
        notNull(sellOrder);

        buyOrder.recordTraded(matchedTradeAmount, tradedDate);
        sellOrder.recordTraded(matchedTradeAmount, tradedDate);

        buyOrderRepository.save(buyOrder);
        sellOrderRepository.save(sellOrder);
    }


    @Autowired
    public void setBuyOrderRepository(BuyOrderRepository buyOrderRepository) {
        this.buyOrderRepository = buyOrderRepository;
    }

    @Autowired
    public void setSellOrderRepository(SellOrderRepository sellOrderRepository) {
        this.sellOrderRepository = sellOrderRepository;
    }
}
