package com.icoin.trading.api.fee.domain.transfer;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:16
 * To change this template use File | Settings | File Templates.
 */
public enum TransferTransactionType implements ValueObject<TransferTransactionType> {
    MONEY,
    COIN;

    @Override
    public boolean sameValueAs(TransferTransactionType other) {
        return this == other;
    }

    @Override
    public TransferTransactionType copy() {
        return this;
    }
}
