package com.icoin.trading.tradeengine.query.tradeexecuted;

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


    public static TradeType convert(com.icoin.trading.tradeengine.domain.model.order.TradeType tradeType) {
        switch (tradeType) {
            case BUY:
                return BUY;
            case SELL:
                return SELL;
            default:
                throw new IllegalArgumentException("Cannot transfer trade type" + tradeType);
        }
    }

}
