package com.icoin.trading.fee.query.fee.paid;

import com.icoin.trading.api.fee.domain.PaidMode;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCreatedEvent;
import com.icoin.trading.fee.query.fee.FeeEntry;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:22
 * To change this template use File | Settings | File Templates.
 */
public class PaidFeeEntry extends FeeEntry<PaidFeeEntry> {
    private PaidMode paidMode;
    private String sequenceNumber;

    public void copy(PaidFeeCreatedEvent event) {
        super.copy(event);
        paidMode = event.getPaidMode();
    }

    public PaidMode getPaidMode() {
        return paidMode;
    }

    public void setPaidMode(PaidMode paidMode) {
        this.paidMode = paidMode;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
