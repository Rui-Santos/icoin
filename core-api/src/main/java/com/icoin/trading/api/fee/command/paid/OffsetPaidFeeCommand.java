package com.icoin.trading.api.fee.command.paid;

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
public class OffsetPaidFeeCommand extends OffsetFeeCommand<OffsetPaidFeeCommand> {
    public OffsetPaidFeeCommand(FeeId feeId, OffsetId offsetId, Date offsetedDate) {
        super(feeId, offsetId, offsetedDate);
    }
}
