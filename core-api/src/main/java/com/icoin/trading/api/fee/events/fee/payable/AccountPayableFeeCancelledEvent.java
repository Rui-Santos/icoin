package com.icoin.trading.api.fee.events.fee.payable;

import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.events.fee.FeeCancelledEvent;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM7:14
 * To change this template use File | Settings | File Templates.
 */
public class AccountPayableFeeCancelledEvent extends FeeCancelledEvent<AccountPayableFeeCancelledEvent> {
    public AccountPayableFeeCancelledEvent(FeeId feeId, CancelledReason cancelledReason, Date cancelledDate) {
        super(feeId, cancelledReason, cancelledDate);
    }
}
