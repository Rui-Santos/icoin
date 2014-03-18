package com.icoin.trading.api.fee.events.offset;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.fee.domain.offset.OffsetId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM12:42
 * To change this template use File | Settings | File Templates.
 */
public class FeesOffsetedEvent extends EventSupport<FeesOffsetedEvent> {
    private final OffsetId offsetId;
    private final Date offsetedDate;

    public FeesOffsetedEvent(OffsetId offsetId, Date offsetedDate) {
        this.offsetId = offsetId;
        this.offsetedDate = offsetedDate;
    }

    public Date getOffsetedDate() {
        return offsetedDate;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }
}
