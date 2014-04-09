package com.icoin.trading.fee.domain.cash;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: PM9:34
 * To change this template use File | Settings | File Templates.
 */
public class PayCash<T extends PayCash> extends Cash<T> {
    private String sequenceNumber;

    public void confirm(String sequenceNumber, Date confirmedDate) {
        this.sequenceNumber = sequenceNumber;
        this.confirmedDate = confirmedDate;
        this.status = CashStatus.COMPLETE;
    }
}
