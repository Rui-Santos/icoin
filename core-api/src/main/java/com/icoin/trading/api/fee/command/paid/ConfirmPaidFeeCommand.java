package com.icoin.trading.api.fee.command.paid;

import com.icoin.trading.api.fee.command.ConfirmFeeCommand;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM12:28
 * To change this template use File | Settings | File Templates.
 */
public class ConfirmPaidFeeCommand extends ConfirmFeeCommand<ConfirmPaidFeeCommand> {
    @NotEmpty
    private String sequenceNumber;

    public ConfirmPaidFeeCommand(FeeId feeId, String sequenceNumber, Date confirmedDate) {
        super(feeId, confirmedDate);
        this.sequenceNumber = sequenceNumber;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }
}
