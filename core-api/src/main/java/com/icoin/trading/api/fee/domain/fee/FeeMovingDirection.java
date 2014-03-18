package com.icoin.trading.api.fee.domain.fee;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:29
 * To change this template use File | Settings | File Templates.
 */
public enum FeeMovingDirection implements ValueObject<FeeMovingDirection> {
    MOVING_IN,
    MOVING_OUT,
    NONE;

    @Override
    public boolean sameValueAs(FeeMovingDirection other) {
        return this == other;
    }

    @Override
    public FeeMovingDirection copy() {
        return this;
    }
}