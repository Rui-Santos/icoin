package com.icoin.trading.api.fee.command.receivable;

import com.icoin.trading.api.fee.command.CreateFeeCommand;

import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import org.joda.money.BigMoney;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:28
 * To change this template use File | Settings | File Templates.
 */
public class CreateAccountReceivableFeeCommand<T extends CreateAccountReceivableFeeCommand> extends CreateFeeCommand<T> {

    public CreateAccountReceivableFeeCommand(FeeTransactionId feeTransactionId,
                                             FeeId feeId,
                                             FeeStatus feeStatus,
                                             BigMoney amount,
                                             FeeType feeType,
                                             BusinessType businessType,
                                             Date createdTime,
                                             Date dueDate,
                                             String userAccountId,
                                             String businessReferenceId) {
        super(feeTransactionId, feeId, feeStatus, amount, feeType, businessType, createdTime, dueDate, userAccountId, businessReferenceId);
    }
}