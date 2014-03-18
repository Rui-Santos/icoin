package com.icoin.trading.api.fee.domain;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:32
 * To change this template use File | Settings | File Templates.
 */
public enum  CommissionType implements ValueObject<CommissionType> {
    BUY,
    SELL;

    @Override
    public boolean sameValueAs(CommissionType tradeType) {
        return tradeType == this;
    }

    @Override
    public CommissionType copy() {
        return this;
    }
}
