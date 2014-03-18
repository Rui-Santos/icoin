package com.icoin.trading.api.fee.domain.offset;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:34
 * To change this template use File | Settings | File Templates.
 */
public class CancelledOperations implements OffsetStatusOperations {
    @Override
    public OffsetStatus offset() {
        throw new UnsupportedOffsetStatusTransitionException(String.format("This Operation %s is not allowed here for offset Status %s!", "offset", OffsetStatus.CANCELLED),
                "offset",
                OffsetStatus.OFFSETED);
    }

    @Override
    public OffsetStatus cancel() {
        throw new UnsupportedOffsetStatusTransitionException(String.format("This Operation %s is not allowed here for offset Status %s!", "cancel", OffsetStatus.CANCELLED),
                "cancel",
                OffsetStatus.CANCELLED);
    }
}
