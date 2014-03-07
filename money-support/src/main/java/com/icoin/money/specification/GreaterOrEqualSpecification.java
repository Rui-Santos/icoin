package com.icoin.money.specification;

import com.homhon.base.domain.specification.CompositeSpecification;
import org.joda.money.BigMoney;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM9:52
 * To change this template use File | Settings | File Templates.
 */
public class GreaterOrEqualSpecification extends CompositeSpecification<BigMoney> {
    private final BigMoney threshold;

    public GreaterOrEqualSpecification(BigMoney threshold) {
        notNull(threshold);
        this.threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(BigMoney amount) {
        notNull(amount);
        return threshold.compareTo(amount) >= 0;
    }
}
