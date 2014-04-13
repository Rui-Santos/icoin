package com.icoin.trading.api.fee.events.fee;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class FeeOffsetedEvent<T extends FeeOffsetedEvent> extends EventSupport<T> {
    private final FeeId feeId;
    private final OffsetId offsetId;
    private final Date offsetedDate;

    protected FeeOffsetedEvent(FeeId feeId, OffsetId offsetId, Date offsetedDate) {
        this.feeId = feeId;
        this.offsetId = offsetId;
        this.offsetedDate = offsetedDate;
    }

    public FeeId getFeeId() {
        return feeId;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }

    public Date getOffsetedDate() {
        return offsetedDate;
    }
}