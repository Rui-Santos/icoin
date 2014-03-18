package com.icoin.trading.api.fee.domain.offset;

import com.homhon.core.exception.IZookeyException;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:37
 * To change this template use File | Settings | File Templates.
 */
public class UnsupportedOffsetStatusTransitionException extends IZookeyException {
    private String operation;
    private OffsetStatus offsetStatus;

    public UnsupportedOffsetStatusTransitionException(String msg, String operation, OffsetStatus currentStatus) {
        super(msg);
        this.operation = operation;
        this.offsetStatus = currentStatus;
    }

    public String getOperation() {
        return operation;
    }

    public OffsetStatus getCurrentStatus() {
        return offsetStatus;
    }
}
