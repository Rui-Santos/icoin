package com.icoin.trading.api.fee.command;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-24
 * Time: AM10:45
 * To change this template use File | Settings | File Templates.
 */
public class AbstractFeeCommand<T extends AbstractFeeCommand> extends ValueObjectSupport<T> {
    @NotNull
    private FeeTransactionId feeTransactionId;

    @NotNull
    private FeeId feeId;

    public AbstractFeeCommand(FeeTransactionId feeTransactionId, FeeId feeId) {
        this.feeTransactionId = feeTransactionId;
        this.feeId = feeId;
    }

    public FeeTransactionId getFeeTransactionId() {
        return feeTransactionId;
    }

    public FeeId getFeeId() {
        return feeId;
    }
}