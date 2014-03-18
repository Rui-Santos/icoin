package com.icoin.trading.api.fee.command;

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
public class CreateFeeCommand<T extends CreateFeeCommand> extends AbstractFeeCommand<T> {
    private FeeId feeId;
    private FeeStatus feeStatus;
    private BigMoney amount;
    private FeeType feeType;
    private BusinessType businessType;

    private Date createdTime;
    private Date dueDate;
    private String userAccountId;
    private String businessReferenceId;

    public CreateFeeCommand(String transactionId,
                            FeeId feeId,
                            FeeStatus feeStatus,
                            BigMoney amount,
                            FeeType feeType,
                            BusinessType businessType,
                            Date createdTime,
                            Date dueDate,
                            String userAccountId,
                            String businessReferenceId) {
        super(transactionId);
        this.feeId = feeId;
        this.feeStatus = feeStatus;
        this.amount = amount;
        this.feeType = feeType;
        this.businessType = businessType;
        this.createdTime = createdTime;
        this.dueDate = dueDate;
        this.userAccountId = userAccountId;
        this.businessReferenceId = businessReferenceId;
    }

    public FeeStatus getFeeStatus() {
        return feeStatus;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public String getBusinessReferenceId() {
        return businessReferenceId;
    }

    public FeeId getFeeId() {
        return feeId;
    }
}