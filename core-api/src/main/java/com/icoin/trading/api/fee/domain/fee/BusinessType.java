package com.icoin.trading.api.fee.domain.fee;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:29
 * To change this template use File | Settings | File Templates.
 */
public enum BusinessType implements ValueObject<BusinessType> {
    RESERVE_COIN,
    RESERVE_MOENY,
    TRADE_EXECUTED,
    CHARGE_COIN_COMMISSION,
    CHARGE_MONEY_COMMISSION,
    WITHDRAW_COIN_COMMISSION,
    WITHDRAW_MONEY_COMMISSION,
    INTEREST_MONEY;

    @Override
    public boolean sameValueAs(BusinessType other) {
        return this == other;
    }

    @Override
    public BusinessType copy() {
        return this;
    }
}