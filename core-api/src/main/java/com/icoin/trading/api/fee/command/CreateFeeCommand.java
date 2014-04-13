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
    private String portfolioId;
    @NotEmpty
    private String userId;
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
                            String portfolioId,
                            String userId,
                            String businessReferenceId) {
        super(feeTransactionId, feeId);
        this.feeStatus = feeStatus;
        this.amount = amount;
        this.feeType = feeType;
        this.businessType = businessType;
        this.createdTime = createdTime;
        this.dueDate = dueDate;
        this.portfolioId = portfolioId;
        this.userId = userId;
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

    public String getPortfolioId() {
        return portfolioId;
    }

    public String getBusinessReferenceId() {
        return businessReferenceId;
    }

    public String getUserId() {
        return userId;
    }
}