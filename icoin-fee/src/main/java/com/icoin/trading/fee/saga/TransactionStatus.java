package com.icoin.trading.fee.saga;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM12:03
 * To change this template use File | Settings | File Templates.
 */

enum TransactionStatus implements ValueObject<TransactionStatus> {
    NEW,
    CREATED,
    CONFIRMED,
    OFFSETED,
    CANCELLED;

    public boolean sameValueAs(TransactionStatus other) {
        return this == other;
    }

    public TransactionStatus copy() {
        return this;
    }
}
