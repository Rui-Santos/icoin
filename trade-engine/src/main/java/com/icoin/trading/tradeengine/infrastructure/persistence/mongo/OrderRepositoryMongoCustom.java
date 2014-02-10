package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import org.joda.money.BigMoney;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/10/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OrderRepositoryMongoCustom {
    //when the price&time are equal, should put biggest amount first 
    List<Order> findPendingSellOrdersByPriceTime(Date toTime,
                                                 BigMoney price,
                                                 OrderBookId orderBookId,
                                                 int size);

    List<Order> findPlacedPendingOrdersAfter(Date toTime,
                                             OrderBookId orderBookId,
                                             int size);

    Order findPendingOrder(String id);

    Order findLowestPricePendingSellOrder(OrderBookId orderBookId);


    List<Order> findPendingBuyOrdersByPriceTime(Date toTime,
                                                BigMoney price,
                                                OrderBookId orderBookId,
                                                int size);

    Order findHighestPricePendingBuyOrder(OrderBookId orderBookId);
} 