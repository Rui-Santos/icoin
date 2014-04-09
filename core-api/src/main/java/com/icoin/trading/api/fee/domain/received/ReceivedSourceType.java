package com.icoin.trading.api.fee.domain.received;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 8/23/13
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ReceivedSourceType implements ValueObject<ReceivedSourceType> {
    E_CREDIT_CARD,
    E_DEBIT_CARD,
    CASH,
    ALIPAY,
    PAYPAL,
    COIN_ADDRESS,
    INTERNAL_ACCOUNT;

    @Override
    public boolean sameValueAs(ReceivedSourceType other) {
        return this == other;
    }

    @Override
    public ReceivedSourceType copy() {
        return this;
    }
}