package com.icoin.trading.tradeengine.domain.model.order;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-1
 * Time: PM2:32
 * To change this template use File | Settings | File Templates.
 */
public enum OrderType implements ValueObject<OrderType> {
    BUY,
    SELL;

    public boolean sameValueAs(OrderType other) {
        return this == other;
    }

    public OrderType copy() {
        return this;
    }
}
