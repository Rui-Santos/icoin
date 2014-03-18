package com.icoin.trading.api.fee.domain.offset;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:32
 * To change this template use File | Settings | File Templates.
 */
public enum OffsetStatus implements ValueObject<OffsetStatus>, OffsetStatusOperations {
    OFFSETED(new OffsetedOperations()),
    NOT_OFFSETED(new NotOffsetedOperations()),
    CANCELLED(new CancelledOperations());

    private OffsetStatusOperations operations;

    private OffsetStatus(OffsetStatusOperations operations) {
        this.operations = operations;
    }

    public boolean sameValueAs(OffsetStatus other) {
        return this == other;
    }

    public OffsetStatus copy() {
        return this;
    }

    @Override
    public OffsetStatus offset() {
        return operations.offset();
    }

    @Override
    public OffsetStatus cancel() {
        return operations.cancel();
    }
}