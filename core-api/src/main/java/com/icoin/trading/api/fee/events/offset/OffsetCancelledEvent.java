package com.icoin.trading.api.fee.events.offset;

import com.icoin.trading.api.fee.domain.offset.CancelledReason;
import com.icoin.trading.api.fee.domain.offset.OffsetId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:40
 * To change this template use File | Settings | File Templates.
 */
public class OffsetCancelledEvent extends AbstractOffsetEvent<OffsetCancelledEvent> {
    private final CancelledReason cancelledReason;
    private final Date cancelledDate;

    public OffsetCancelledEvent(OffsetId offsetId, CancelledReason cancelledReason, Date cancelledDate) {
        super(offsetId);
        this.cancelledReason = cancelledReason;
        this.cancelledDate = cancelledDate;
    }

    public CancelledReason getCancelledReason() {
        return cancelledReason;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }
}
