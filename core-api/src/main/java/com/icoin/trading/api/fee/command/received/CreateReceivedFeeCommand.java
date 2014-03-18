package com.icoin.trading.api.fee.command.received;


import com.icoin.trading.api.fee.command.CreateFeeCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/14/14
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateReceivedFeeCommand<T extends CreateReceivedFeeCommand> extends CreateFeeCommand<T> {
    @NotNull
    private final ReceivedSource receivedSource;

    public CreateReceivedFeeCommand(FeeTransactionId feeTransactionId,
                                    FeeId feeId,
                                    FeeStatus feeStatus,
                                    BigMoney amount,
                                    FeeType feeType,
                                    BusinessType businessType,
                                    Date createdTime,
                                    Date dueDate,
                                    String userAccountId,
                                    String businessReferenceId,
                                    ReceivedSource receivedSource) {
        super(feeTransactionId, feeId, feeStatus, amount, feeType, businessType, createdTime, dueDate, userAccountId, businessReferenceId);
        this.receivedSource = receivedSource;
    }

    public ReceivedSource getReceivedSource() {
        return receivedSource;
    }
}