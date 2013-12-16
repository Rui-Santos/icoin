package com.icoin.trading.tradeengine.domain.model.order;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-9
 * Time: PM9:35
 * To change this template use File | Settings | File Templates.
 */
public enum TradeType implements ValueObject<TradeType> {
    BUY,
    SELL;

    @Override
    public boolean sameValueAs(TradeType tradeType) {
        return tradeType == this;
    }

    @Override
    public TradeType copy() {
        return this;
    }
}
