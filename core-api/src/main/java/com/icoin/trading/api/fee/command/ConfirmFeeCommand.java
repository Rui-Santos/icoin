package com.icoin.trading.api.fee.command;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.fee.domain.fee.FeeId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class ConfirmFeeCommand<T extends ConfirmFeeCommand> extends CommandSupport<T> {
    private final FeeId feeId;
    private final Date confirmedDate;

    protected ConfirmFeeCommand(FeeId feeId, Date confirmedDate) {
        this.feeId = feeId;
        this.confirmedDate = confirmedDate;
    }

    public FeeId getFeeId() {
        return feeId;
    }

    public Date getConfirmedDate() {
        return confirmedDate;
    }
}