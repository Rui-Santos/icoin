package com.icoin.trading.fee.domain.cash;

import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: PM9:34
 * To change this template use File | Settings | File Templates.
 */
public class ReceiveCash<T extends ReceiveCash> extends Cash<T> {
    public void confirm(BigMoney amount, Date confirmedDate) {
        this.amount = amount;
        this.confirmedDate = confirmedDate;
        this.status = CashStatus.COMPLETE;
    }
}
