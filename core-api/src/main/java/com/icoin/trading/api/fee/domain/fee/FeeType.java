package com.icoin.trading.api.fee.domain.fee;

import com.homhon.base.domain.ValueObject;

import static com.homhon.util.Asserts.hasText;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:30
 * To change this template use File | Settings | File Templates.
 */
public enum FeeType implements ValueObject<FeeType> {
    RESERVED(AccountingType.CREDIT, "Reserve for traders"),
    BOUGHT_MONEY_COMMISSION(AccountingType.CREDIT, "Commission for the execution"),
    SOLD_COIN_COMMISSION(AccountingType.CREDIT, "Commission for the execution"),
    PAY_MONEY(AccountingType.CREDIT, "Pay money"),
    PAY_COIN(AccountingType.CREDIT, "Pay coin"),
    RECEIVE_MONEY(AccountingType.DEBIT, "Sold money"),
    RECEIVE_COIN(AccountingType.DEBIT, "Sold coin"),
    RESERVE_MONEY(AccountingType.DEBIT, "Reserve money"),
    RESERVE_COIN(AccountingType.DEBIT, "Reserve coin"),
    //    INTEREST(AccountingType.CREDIT, "Interest from bank"),
//    REFUND(AccountingType.DEBIT, "Refund to traders"),
    REPAY(AccountingType.DEBIT, "Repay the commission to consumers");

    private AccountingType accountType;
    private String desc;

    private FeeType(AccountingType accountType, String desc) {
        notNull(accountType);
        hasText(desc);
        this.accountType = accountType;
        this.desc = desc;
    }

    public AccountingType getAccountType() {
        return accountType;
    }

    public String getDesc() {
        return desc;
    }

    public boolean sameValueAs(FeeType other) {
        return this == other;
    }

    public FeeType copy() {
        return this;
    }
}