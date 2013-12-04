package com.icoin.trading.tradeengine.domain.model.order;

import com.homhon.base.domain.repository.GenericCrudRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-2
 * Time: PM10:54
 * To change this template use File | Settings | File Templates.
 */
public interface BuyOrderRepository extends GenericCrudRepository<BuyOrder, String> {
    //when the price&time are equal, should put biggest amount first
    List<BuyOrder> findDescPendingOrdersByPriceTime(Date toTime,
                                                    BigDecimal price,
                                                    OrderBookId orderBookId,
                                                    int size);

    SellOrder findPendingOrder(String id);

    SellOrder findHighPricePendingOrder();
}
