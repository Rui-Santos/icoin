package com.icoin.trading.api.fee.domain.offset;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:32
 * To change this template use File | Settings | File Templates.
 */
public interface OffsetStatusOperations {
    OffsetStatus offset();

    OffsetStatus cancel();
}
