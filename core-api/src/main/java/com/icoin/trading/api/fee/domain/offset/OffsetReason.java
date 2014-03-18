package com.icoin.trading.api.fee.domain.offset;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:38
 * To change this template use File | Settings | File Templates.
 */
public enum OffsetReason implements ValueObject<OffsetReason> {
    RESERVED,
    CHARGE,
    FUND,
    REFUND;

    @Override
    public boolean sameValueAs(OffsetReason other) {
        return this == other;
    }

    @Override
    public OffsetReason copy() {
        return this;
    }
}