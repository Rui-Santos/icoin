package com.icoin.trading.api.fee.command;

import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import org.hibernate.validator.constraints.NotEmpty;
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
public class CreateFeeCommand<T extends CreateFeeCommand> extends AbstractFeeCommand<T> {
    @NotNull
    private FeeStatus feeStatus;
    @NotNull
    private BigMoney amount;
    @NotNull
    private FeeType feeType;
    @NotNull
    private BusinessType businessType;

    @NotNull
    private Date createdTime;
    @NotNull
    private Date dueDate;
    @NotEmpty
    private String userAccountId;
    @NotNull
    private String businessReferenceId;

    public CreateFeeCommand(FeeTransactionId feeTransactionId,
                            FeeId feeId,
                            FeeStatus feeStatus,
                            BigMoney amount,
                            FeeType feeType,
                            BusinessType businessType,
                            Date createdTime,
                            Date dueDate,
                            String userAccountId,
                            String businessReferenceId) {
        super(feeTransactionId, feeId);
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
}