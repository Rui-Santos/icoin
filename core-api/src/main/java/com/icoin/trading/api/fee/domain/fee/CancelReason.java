package com.icoin.trading.api.fee.domain.fee;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:30
 * To change this template use File | Settings | File Templates.
 */
public enum CancelReason implements ValueObject<CancelReason> {
    DUPLICATED("Duplicated fee found"),
    OFFSET_ERROR("Error When offset");

    private final String desc;

    private CancelReason(String desc) {
        this.desc = desc;
    }

    public boolean sameValueAs(CancelReason other) {
        return this == other;
    }

    public CancelReason copy() {
        return this;
    }
}