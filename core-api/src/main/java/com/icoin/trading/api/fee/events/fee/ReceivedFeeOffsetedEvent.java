package com.icoin.trading.api.fee.events.fee;

import com.icoin.trading.api.fee.domain.fee.FeeId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedFeeOffsetedEvent extends FeeOffsetedEvent<ReceivedFeeOffsetedEvent> {

    protected ReceivedFeeOffsetedEvent(FeeId feeId, Date offsetedDate) {
        super(feeId, offsetedDate);
    }
}