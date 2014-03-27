package com.icoin.trading.api.fee.events.fee.receivable;

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
public class AccountReceivableFeeConfirmedEvent extends FeeConfirmedEvent<AccountReceivableFeeConfirmedEvent> {

    public AccountReceivableFeeConfirmedEvent(FeeId feeId, Date confirmedDate) {
        super(feeId, confirmedDate);
    }
}