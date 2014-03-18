package com.icoin.trading.fee.domain.fee;

import com.homhon.base.domain.specification.CompositeSpecification;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-31
 * Time: PM12:37
 * To change this template use File | Settings | File Templates.
 */
public class ConfirmedSpecification extends CompositeSpecification<AbstractFee> {
    @Override
    public boolean isSatisfiedBy(AbstractFee fee) {
        notNull(fee);
        return FeeStatus.CONFIRMED == fee.getFeeStatus();
    }
}
