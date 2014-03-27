package com.icoin.trading.api.fee.events.fee.paid;

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
public class PaidFeeCancelledEvent extends FeeCancelledEvent<PaidFeeCancelledEvent> {
    public PaidFeeCancelledEvent(FeeId feeId, CancelledReason cancelledReason, Date cancelledDate) {
        super(feeId, cancelledReason, cancelledDate);
    }
}
