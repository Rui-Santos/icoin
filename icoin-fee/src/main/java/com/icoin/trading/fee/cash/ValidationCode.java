package com.icoin.trading.fee.cash;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM8:55
 * To change this template use File | Settings | File Templates.
 */
public enum ValidationCode implements ValueObject<ValidationCode> {
    SUCCESSFUL,
    EXECUTION_ERROR,
    EXCEEDED_MAX_TIMES_PER_DAY,
    EXCEEDED_MAX_AMOUNT_PER_TIME,
    EXCEEDED_MIN_AMOUNT_PER_TIME,
    EXCEEDED_MAX_AMOUNT_PER_DAY,
    EXCEEDED_GLOBAL_MAX_AMOUNT_PER_DAY,
    USER_NOT_FOUND,
    USER_LOCKED,
    SYSTEM_DISALLOWED;

    @Override
    public boolean sameValueAs(ValidationCode other) {
        return other == this;
    }

    @Override
    public ValidationCode copy() {
        return this;
    }

    public static boolean breakDown(ValidationCode code) {
        return code != null && code != ValidationCode.SUCCESSFUL;
    }
}