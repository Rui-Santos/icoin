package com.icoin.trading.tradeengine.domain.events.order;

import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;

import com.homhon.base.domain.event.EventSupport;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-4
 * Time: AM9:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractRefreshedPriceEvent<T extends AbstractRefreshedPriceEvent> extends EventSupport<T> {
    private OrderBookId orderBookId;
    private BigDecimal price;

    public AbstractRefreshedPriceEvent(OrderBookId orderBookId, BigDecimal price) {
        this.orderBookId = orderBookId;
        this.price = price;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
