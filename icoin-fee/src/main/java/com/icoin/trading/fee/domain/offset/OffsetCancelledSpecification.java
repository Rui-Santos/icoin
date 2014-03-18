package com.icoin.trading.fee.domain.offset;

import com.icoin.trading.api.fee.domain.offset.OffsetStatus;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-31
 * Time: PM12:37
 * To change this template use File | Settings | File Templates.
 */
public class OffsetCancelledSpecification extends OffsetStatusSpecification {

    public OffsetCancelledSpecification() {
        super(OffsetStatus.CANCELLED);
    }
}
