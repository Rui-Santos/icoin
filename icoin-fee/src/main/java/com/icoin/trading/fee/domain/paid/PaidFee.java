package com.icoin.trading.fee.domain.paid;

import com.icoin.trading.fee.domain.fee.FeeAggregateRoot;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:24
 * To change this template use File | Settings | File Templates.
 */
public class PaidFee extends FeeAggregateRoot<PaidFee> {
    private PaidMode paidMode;

    @SuppressWarnings("UnusedDeclaration")
    protected PaidFee() {
    }
}
