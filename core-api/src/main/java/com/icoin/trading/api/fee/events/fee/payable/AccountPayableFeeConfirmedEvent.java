package com.icoin.trading.api.fee.events.fee.payable;

import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.events.fee.FeeConfirmedEvent;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class AccountPayableFeeConfirmedEvent extends FeeConfirmedEvent<AccountPayableFeeConfirmedEvent> {

    public AccountPayableFeeConfirmedEvent(FeeId feeId, Date confirmedDate) {
        super(feeId, confirmedDate);
    }
}