package com.icoin.trading.api.fee.command.receivable;

import com.icoin.trading.api.fee.command.OffsetFeeCommand;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM12:28
 * To change this template use File | Settings | File Templates.
 */
public class OffsetAccountReceivableFeeCommand extends OffsetFeeCommand<OffsetAccountReceivableFeeCommand> {
    public OffsetAccountReceivableFeeCommand(FeeId feeId, OffsetId offsetId, Date offsetedDate) {
        super(feeId, offsetId, offsetedDate);
    }
}
