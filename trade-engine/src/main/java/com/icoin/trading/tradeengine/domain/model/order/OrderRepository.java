package com.icoin.trading.tradeengine.domain.model.order;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import org.joda.money.BigMoney;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-2
 * Time: PM10:54
 * To change this template use File | Settings | File Templates.
 */
public interface OrderRepository extends GenericCrudRepository<Order, String> {
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