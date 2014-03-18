package com.icoin.trading.api.fee.events.offset;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.fee.domain.offset.OffsetId;


import static com.homhon.util.Asserts.notNull;
/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:41
 * To change this template use File | Settings | File Templates.
 */
public class AbstractOffsetEvent<T extends AbstractOffsetEvent> extends EventSupport<T>{
    private final OffsetId offsetId;

    public AbstractOffsetEvent(OffsetId offsetId) {
        notNull(offsetId);
        this.offsetId = offsetId;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }
}
