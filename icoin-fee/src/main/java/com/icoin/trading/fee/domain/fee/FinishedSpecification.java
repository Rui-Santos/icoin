package com.icoin.trading.fee.domain.fee;

import com.homhon.base.domain.specification.CompositeSpecification;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 9/2/13
 * Time: 3:27 PM
 * Cancelled or complete.
 */
public class FinishedSpecification extends CompositeSpecification<AbstractFee> {
    @Override
    public boolean isSatisfiedBy(AbstractFee fee) {
        notNull(fee);
        return new CancelledSpecification()
                .or(new CompleteSpecification()).isSatisfiedBy(fee);
    }
}