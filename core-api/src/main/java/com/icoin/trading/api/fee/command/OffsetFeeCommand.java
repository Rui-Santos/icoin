package com.icoin.trading.api.fee.command;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class OffsetFeeCommand<T extends OffsetFeeCommand> extends CommandSupport<T> {
    @NotNull
    private final FeeId feeId;
    @NotNull
    private final OffsetId offsetId;
    @NotNull
    private final Date offsetedDate;

    protected OffsetFeeCommand(FeeId feeId, OffsetId offsetId, Date offsetedDate) {
        this.feeId = feeId;
        this.offsetId = offsetId;
        this.offsetedDate = offsetedDate;
    }

    public FeeId getFeeId() {
        return feeId;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }

    public Date getOffsetedDate() {
        return offsetedDate;
    }
}