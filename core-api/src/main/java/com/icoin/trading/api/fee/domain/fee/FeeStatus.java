package com.icoin.trading.api.fee.domain.fee;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-17
 * Time: PM9:37
 * To change this template use File | Settings | File Templates.
 */
public enum FeeStatus implements ValueObject<FeeStatus> {
    CONFIRMED,
    CANCELLED,
    PENDING;

    public boolean sameValueAs(FeeStatus other) {
        return this == other;
    }

    public FeeStatus copy() {
        return this;
    }
}