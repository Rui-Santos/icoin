package com.icoin.trading.tradeengine.query.activity;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM12:52
 * To change this template use File | Settings | File Templates.
 */
public enum PortfolioAlarmType implements ValueObject<PortfolioAlarmType> {
    WITHDRAW_LARGE_AMOUNT_OF_MONEY,
    ADD_LARGE_AMOUNT_OF_MONEY,
    WITHDRAW_LARGE_AMOUNT_OF_COIN,
    ADD_LARGE_AMOUNT_OF_COIN;

    @Override
    public boolean sameValueAs(PortfolioAlarmType other) {
        return other == this;
    }

    @Override
    public PortfolioAlarmType copy() {
        return this;
    }
}