package com.icoin.trading.api.fee.command.payable;

import com.icoin.trading.api.fee.command.CancelFeeCommand;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM12:28
 * To change this template use File | Settings | File Templates.
 */
public class CancelAccountPayableFeeCommand
        extends CancelFeeCommand<CancelAccountPayableFeeCommand> {

    public CancelAccountPayableFeeCommand(FeeId feeId, CancelledReason cancelledReason, Date cancelledDate) {
        super(feeId, cancelledReason, cancelledDate);
    }
}
