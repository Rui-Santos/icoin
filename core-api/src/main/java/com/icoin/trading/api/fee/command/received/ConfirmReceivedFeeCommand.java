package com.icoin.trading.api.fee.command.received;

import com.icoin.trading.api.fee.command.ConfirmFeeCommand;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM12:28
 * To change this template use File | Settings | File Templates.
 */
public class ConfirmReceivedFeeCommand extends ConfirmFeeCommand<ConfirmReceivedFeeCommand> {
    @NotNull
    private BigMoney amount;

    public ConfirmReceivedFeeCommand(FeeId feeId, BigMoney amount, Date confirmedDate) {
        super(feeId, confirmedDate);
        isTrue(amount.isPositive());
        this.amount = amount;
    }

    public BigMoney getAmount() {
        return amount;
    }
}
