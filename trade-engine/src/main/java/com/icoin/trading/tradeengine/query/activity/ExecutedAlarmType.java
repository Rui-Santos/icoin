package com.icoin.trading.tradeengine.query.activity;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM12:52
 * To change this template use File | Settings | File Templates.
 */
public enum ExecutedAlarmType implements ValueObject<ExecutedAlarmType> {
    PRICE,
    MONEY,
    AMOUNT;

    @Override
    public boolean sameValueAs(ExecutedAlarmType other) {
        return other == this;
    }

    @Override
    public ExecutedAlarmType copy() {
        return this;
    }
}