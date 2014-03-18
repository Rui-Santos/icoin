package com.icoin.trading.api.fee.command.offset;

import com.homhon.base.command.CommandSupport;
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
public class OffsetFeesCommand extends CommandSupport<OffsetFeesCommand> {
    @NotNull
    private final OffsetId offsetId;
    @NotNull
    private final Date offsetedDate;

    public OffsetFeesCommand(OffsetId offsetId, Date offsetedDate) {
        this.offsetId = offsetId;
        this.offsetedDate = offsetedDate;
    }

    public Date getOffsetedDate() {
        return offsetedDate;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }
}
