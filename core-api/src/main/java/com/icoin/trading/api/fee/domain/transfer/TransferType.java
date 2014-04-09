package com.icoin.trading.api.fee.domain.transfer;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:12
 * To change this template use File | Settings | File Templates.
 */
public enum TransferType implements ValueObject<TransferType> {
    IN,
    OUT;

    @Override
    public boolean sameValueAs(TransferType other) {
        return this == other;
    }

    @Override
    public TransferType copy() {
        return this;
    }
}