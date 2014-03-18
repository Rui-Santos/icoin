package com.icoin.trading.api.fee.events.offset;

import com.icoin.trading.api.fee.domain.offset.CancelledReason;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.offset.OffsetReason;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:40
 * To change this template use File | Settings | File Templates.
 */
public class OffsetCancelledEvent extends AbstractOffsetEvent<OffsetCancelledEvent>{
    private final CancelledReason cancelledReason;
    private final Date date;

    public OffsetCancelledEvent(OffsetId offsetId, CancelledReason cancelledReason, Date date) {
        super(offsetId);
        this.cancelledReason = cancelledReason;
        this.date = date;
    }

    public CancelledReason getCancelledReason() {
        return cancelledReason;
    }

    public Date getDate() {
        return date;
    }
}
