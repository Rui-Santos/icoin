package com.icoin.trading.fee.query.fee;

import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.events.fee.ReceivedFeeCreatedEvent;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:22
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedFeeEntry extends FeeEntry<ReceivedFeeEntry> {
    private ReceivedSource receivedSource;

    public void copy(ReceivedFeeCreatedEvent event) {
        super.copy(event);
        receivedSource = event.getReceivedSource();
    }

    public ReceivedSource getReceivedSource() {
        return receivedSource;
    }

    public void setReceivedSource(ReceivedSource receivedSource) {
        this.receivedSource = receivedSource;
    }
}
