package com.icoin.trading.fee.cash;

import com.homhon.base.domain.ValueObject;
/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM8:55
 * To change this template use File | Settings | File Templates.
 */
public enum ResultCode implements ValueObject<ResultCode> {
    COMPLETE,
    EXECUTION_ERROR;

    @Override
    public boolean sameValueAs(ResultCode other) {
        return other == this;
    }

    @Override
    public ResultCode copy() {
        return this;
    }

    public static boolean breakDown(ResultCode code) {
        return code != null && code != ResultCode.COMPLETE;
    }
}