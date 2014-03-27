package com.icoin.trading.api.fee.domain.fee;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:29
 * To change this template use File | Settings | File Templates.
 */
public enum AccountingType implements ValueObject<AccountingType> {
    DEBIT,//DR
    CREDIT,//CR
    NONE;

    @Override
    public boolean sameValueAs(AccountingType other) {
        return this == other;
    }

    @Override
    public AccountingType copy() {
        return this;
    }
}