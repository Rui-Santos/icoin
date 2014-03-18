package com.icoin.trading.api.fee.command.offset;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.fee.domain.offset.CancelledReason;
import com.icoin.trading.api.fee.domain.offset.OffsetId;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM12:42
 * To change this template use File | Settings | File Templates.
 */
public class CancelOffsetCommand extends CommandSupport<CancelOffsetCommand> {
    @NotNull
    private final OffsetId offsetId;
    @NotNull
    private final CancelledReason cancelledReason;
    @NotNull
    private final Date cancelledDate;

    public CancelOffsetCommand(OffsetId offsetId, CancelledReason cancelledReason, Date cancelledDate) {
        this.offsetId = offsetId;
        this.cancelledReason = cancelledReason;
        this.cancelledDate = cancelledDate;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public CancelledReason getCancelledReason() {
        return cancelledReason;
    }
}
