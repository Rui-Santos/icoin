package com.icoin.trading.fee.domain.offset;

import com.homhon.base.domain.specification.CompositeSpecification;
import com.icoin.trading.api.fee.domain.offset.OffsetStatus;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-31
 * Time: PM12:37
 * To change this template use File | Settings | File Templates.
 */
public class OffsetStatusSpecification extends CompositeSpecification<Offset> {
    private final OffsetStatus offsetStatus;

    public OffsetStatusSpecification(OffsetStatus offsetStatus) {
        notNull(offsetStatus);
        this.offsetStatus = offsetStatus;
    }

    @Override
    public boolean isSatisfiedBy(Offset offset) {
        notNull(offset);
        return offsetStatus == offset.getOffsetStatus();
    }
}
