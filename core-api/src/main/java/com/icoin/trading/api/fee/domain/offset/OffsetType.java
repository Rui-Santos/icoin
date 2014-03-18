package com.icoin.trading.api.fee.domain.offset;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:31
 * To change this template use File | Settings | File Templates.
 */
public enum OffsetType implements ValueObject<OffsetType> {
    RECEIVED_AR,
    AP_PAID;

    @Override
    public boolean sameValueAs(OffsetType other) {
        return this == other;
    }

    @Override
    public OffsetType copy() {
        return this;
    }
}
