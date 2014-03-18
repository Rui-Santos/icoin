package com.icoin.trading.api.fee.domain.fee;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:30
 * To change this template use File | Settings | File Templates.
 */
public enum FeeType implements ValueObject<FeeType> {
    RESERVED(FeeMovingDirection.MOVING_IN, "Reserve for traders"),
    BUY_COMMISSION(FeeMovingDirection.MOVING_IN, "Commission for the execution"),
    SELL_COMMISSION(FeeMovingDirection.MOVING_IN, "Commission for the execution"),
    INTEREST(FeeMovingDirection.MOVING_IN, "Interest from bank"),
    REFUND(FeeMovingDirection.MOVING_OUT, "Refund to traders"),
    REPAY(FeeMovingDirection.MOVING_OUT, "Repay the commission to consumers");

    private FeeMovingDirection movingDirection;
    private String desc;

    private FeeType(FeeMovingDirection movingDirection, String desc) {
        this.movingDirection = movingDirection;
        this.desc = desc;
    }

    public FeeMovingDirection getMovingDirection() {
        return movingDirection;
    }

    public boolean sameValueAs(FeeType other) {
        return this == other;
    }

    public FeeType copy() {
        return this;
    }
}