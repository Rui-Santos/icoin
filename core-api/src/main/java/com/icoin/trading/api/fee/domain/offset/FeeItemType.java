package com.icoin.trading.api.fee.domain.offset;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:29
 * To change this template use File | Settings | File Templates.
 */
public enum FeeItemType implements ValueObject<FeeItemType> {
    RECEIVED,
    PAID,
    AR,
    AP;

    @Override
    public boolean sameValueAs(FeeItemType other) {
        return this == other;
    }

    @Override
    public FeeItemType copy() {
        return this;
    }
}