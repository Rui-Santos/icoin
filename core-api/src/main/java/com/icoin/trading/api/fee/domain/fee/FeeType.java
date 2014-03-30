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
    RESERVED(AccountingType.CREDIT, "Reserve for traders"),
    BUY_COMMISSION(AccountingType.CREDIT, "Commission for the execution"),
    SOLD_MONEY(AccountingType.CREDIT, "Sold money"),
    SOLD_COIN(AccountingType.CREDIT, "Sold coin"),
    SELL_COMMISSION(AccountingType.CREDIT, "Commission for the execution"),
    INTEREST(AccountingType.CREDIT, "Interest from bank"),
    REFUND(AccountingType.DEBIT, "Refund to traders"),
    REPAY(AccountingType.CREDIT, "Repay the commission to consumers");

    private AccountingType movingDirection;
    private String desc;

    private FeeType(AccountingType movingDirection, String desc) {
        this.movingDirection = movingDirection;
        this.desc = desc;
    }

    public AccountingType getMovingDirection() {
        return movingDirection;
    }

    public boolean sameValueAs(FeeType other) {
        return this == other;
    }

    public FeeType copy() {
        return this;
    }
}