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
public class OffsetFeeCommand<T extends OffsetFeeCommand> extends CommandSupport<T> {
    private final FeeId feeId;
    private final Date offsetedDate;

    protected OffsetFeeCommand(FeeId feeId, Date offsetedDate) {
        this.feeId = feeId;
        this.offsetedDate = offsetedDate;
    }

    public FeeId getFeeId() {
        return feeId;
    }

    public Date getOffsetedDate() {
        return offsetedDate;
    }
}