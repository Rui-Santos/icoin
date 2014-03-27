package com.icoin.trading.api.fee.domain;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:32
 * To change this template use File | Settings | File Templates.
 */
public enum ExecutedFeeType implements ValueObject<ExecutedFeeType> {
    BUY_COMMISSION,
    SELL_COMMISSION,
    BUY,
    SELL;

    @Override
    public boolean sameValueAs(ExecutedFeeType tradeType) {
        return tradeType == this;
    }

    @Override
    public ExecutedFeeType copy() {
        return this;
    }
}
