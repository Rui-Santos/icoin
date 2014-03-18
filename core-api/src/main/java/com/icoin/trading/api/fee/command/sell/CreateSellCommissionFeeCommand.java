package com.icoin.trading.api.fee.command.sell;

import com.icoin.trading.api.fee.command.CreateFeeCommand;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/14/14
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateSellCommissionFeeCommand extends CreateFeeCommand<CreateSellCommissionFeeCommand> {

    public CreateSellCommissionFeeCommand(String transactionId,
                                          FeeId feeId,
                                          BigMoney amount,
                                          Date createdTime,
                                          Date dueDate,
                                          String userAccountId,
                                          String businessReferenceId) {
        super(transactionId,
                feeId,
                FeeStatus.PENDING,
                amount,
                FeeType.SELL_COMMISSION,
                BusinessType.SELL_COMMISSION,
                createdTime,
                dueDate,
                userAccountId,
                businessReferenceId);
    }
}