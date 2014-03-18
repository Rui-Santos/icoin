package com.icoin.trading.api.fee.command;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class CancelFeeCommand<T extends CancelFeeCommand> extends CommandSupport<T> {
    @NotNull
    private final FeeId feeId;
    @NotNull
    private final CancelledReason cancelledReason;
    @NotNull
    private final Date cancelledDate;

    public CancelFeeCommand(FeeId feeId, CancelledReason cancelledReason, Date cancelledDate) {
        this.feeId = feeId;
        this.cancelledReason = cancelledReason;
        this.cancelledDate = cancelledDate;
    }

    public FeeId getFeeId() {
        return feeId;
    }

    public CancelledReason getCancelledReason() {
        return cancelledReason;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }
}