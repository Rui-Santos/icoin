package com.icoin.trading.api.fee.domain.offset;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:33
 * To change this template use File | Settings | File Templates.
 */
public class NotOffsetedOperations implements OffsetStatusOperations {
    @Override
    public OffsetStatus offset() {
        return OffsetStatus.OFFSETED;
    }

    @Override
    public OffsetStatus cancel() {
        return OffsetStatus.CANCELLED;
    }
}
