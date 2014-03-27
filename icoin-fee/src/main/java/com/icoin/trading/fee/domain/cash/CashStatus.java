package com.icoin.trading.fee.domain.cash;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM11:36
 * To change this template use File | Settings | File Templates.
 */
public enum CashStatus implements ValueObject<CashStatus> {
    CREATED,
    COMPLETE,
    CANCELLED;

    @Override
    public boolean sameValueAs(CashStatus other) {
        return other == this;
    }

    @Override
    public CashStatus copy() {
        return this;
    }
}
