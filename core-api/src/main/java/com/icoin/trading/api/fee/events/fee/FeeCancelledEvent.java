package com.icoin.trading.api.fee.events.fee;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class FeeCancelledEvent<T extends FeeCancelledEvent> extends EventSupport<T> {
    private final FeeId feeId;
    private final CancelledReason cancelledReason;
    private final Date cancelledDate;

    public FeeCancelledEvent(FeeId feeId, CancelledReason cancelledReason, Date cancelledDate) {
        this.feeId = feeId;
        this.cancelledReason = cancelledReason;
        this.cancelledDate = cancelledDate;
    }

    public FeeId getFeeId() {
        return feeId;
    }

    public CancelledReason getCancelledReason() {
        return cancelledReason;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }
}