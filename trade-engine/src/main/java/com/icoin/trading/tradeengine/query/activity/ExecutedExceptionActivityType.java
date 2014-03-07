package com.icoin.trading.tradeengine.query.activity;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM12:52
 * To change this template use File | Settings | File Templates.
 */
public enum ExecutedExceptionActivityType implements ValueObject<ExecutedExceptionActivityType> {
    PRICE_TOO_HIGH,
    PRICE_TOO_LOW,
    AMOUNT_TOO_SMALL,
    AMOUNT_TOO_LARGE,
    ;

    @Override
    public boolean sameValueAs(ExecutedExceptionActivityType other) {
        return other == this;
    }

    @Override
    public ExecutedExceptionActivityType copy() {
        return this;
    }
}