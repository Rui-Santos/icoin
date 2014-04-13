package com.icoin.trading.api.fee.events.fee.received;

import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.events.fee.FeeConfirmedEvent;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedFeeConfirmedEvent extends FeeConfirmedEvent<ReceivedFeeConfirmedEvent> {
    private BigMoney amount;

    public ReceivedFeeConfirmedEvent(FeeId feeId, BigMoney amount, Date confirmedDate) {
        super(feeId, confirmedDate);
        this.amount = amount;
    }

    public BigMoney getAmount() {
        return amount;
    }
}