package com.icoin.trading.api.tradeengine.events.order;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.tradeengine.events.order.OrderBookId;
import org.joda.money.BigMoney;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-4
 * Time: AM9:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractRefreshedPriceEvent<T extends AbstractRefreshedPriceEvent> extends EventSupport<T> {
    private OrderBookId orderBookId;
    private BigMoney price;

    public AbstractRefreshedPriceEvent(OrderBookId orderBookId, BigMoney price) {
        this.orderBookId = orderBookId;
        this.price = price;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public BigMoney getPrice() {
        return price;
    }
}
