package com.icoin.trading.tradeengine.query.order;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-5
 * Time: AM9:26
 * To change this template use File | Settings | File Templates.
 */
public enum OrderType implements ValueObject<OrderType> {
    BUY,
    SELL;

    @Override
    public boolean sameValueAs(OrderType orderType) {
        return this==orderType;
    }

    @Override
    public OrderType copy() {
        return this;
    }

}
