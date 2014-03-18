package com.icoin.trading.api.fee.events.fee;

import com.homhon.base.domain.event.EventSupport;
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
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class FeeCreatedEvent<T extends FeeCreatedEvent> extends EventSupport<T> {
    private final FeeId feeId;
    private final FeeStatus feeStatus;
    private final BigMoney amount;
    private final FeeType feeType;
    private final Date dueDate;
    private final Date businessCreationTime;
    private final String userAccountId;
    private final BusinessType businessType;
    private final String businessReferenceId;

    protected FeeCreatedEvent(FeeId feeId, FeeStatus feeStatus, BigMoney amount, FeeType feeType, Date dueDate, Date businessCreationTime, String userAccountId, BusinessType businessType, String businessReferenceId) {
        this.feeId = feeId;
        this.feeStatus = feeStatus;
        this.amount = amount;
        this.feeType = feeType;
        this.dueDate = dueDate;
        this.businessCreationTime = businessCreationTime;
        this.userAccountId = userAccountId;
        this.businessType = businessType;
        this.businessReferenceId = businessReferenceId;
    }

    public FeeId getFeeId() {
        return feeId;
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

    public Date getDueDate() {
        return dueDate;
    }

    public Date getBusinessCreationTime() {
        return businessCreationTime;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public String getBusinessReferenceId() {
        return businessReferenceId;
    }
}