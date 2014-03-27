package com.icoin.trading.api.fee.domain;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:32
 * To change this template use File | Settings | File Templates.
 */

public enum PaidMode implements ValueObject<PaidMode> {
    VIA_PAPAL,
    VIA_ALLIPAY,
    VIA_UNION_PAY,
    INTERNAL;

    @Override
    public boolean sameValueAs(PaidMode other) {
        return this == other;
    }

    @Override
    public PaidMode copy() {
        return this;
    }
}