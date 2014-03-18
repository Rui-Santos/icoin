package com.icoin.trading.api.fee.domain.offset;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:38
 * To change this template use File | Settings | File Templates.
 */
public enum CancelledReason implements ValueObject<CancelledReason> {
    AMOUNT_NOT_MATCHED,
    MANUALLY_CANCELLED;

    @Override
    public boolean sameValueAs(CancelledReason other) {
        return this == other;
    }

    @Override
    public CancelledReason copy() {
        return this;
    }
}