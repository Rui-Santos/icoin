package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-6
 * Time: PM9:43
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("UnusedDeclaration")
public interface SellOrderRepositoryMongoCustom {
    List<SellOrder> findAscPendingOrdersByPriceTime(Date toTime, BigDecimal price, OrderBookId orderBookId, int size);

    SellOrder findPendingOrder(String id);

    SellOrder findLowestPricePendingOrder(OrderBookId orderBookId);
}
