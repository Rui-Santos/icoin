package com.icoin.trading.fee.query.fee;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.events.fee.FeeCreatedEvent;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class FeeEntry<T extends FeeEntry> extends VersionedEntitySupport<T, String, Integer> {
    protected FeeStatus feeStatus;
    protected BigMoney amount;
    protected FeeType feeType;
    protected Date dueDate;
    protected Date confirmedDate;
    protected Date cancelledDate;
    protected CancelledReason cancelledReason;
    protected Date businessCreationTime;
    protected boolean offseted;
    protected Date offsetDate;
    protected Date postedDate;
    protected boolean posted;
    protected String userAccountId;
    protected String offsetId;
    protected BusinessType businessType;
    //like order id, like interest rates from back
    protected String businessReferenceId;

    public FeeStatus getFeeStatus() {
        return feeStatus;
    }

    public void setFeeStatus(FeeStatus feeStatus) {
        this.feeStatus = feeStatus;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public void setAmount(BigMoney amount) {
        this.amount = amount;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(Date confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public CancelledReason getCancelledReason() {
        return cancelledReason;
    }

    public void setCancelledReason(CancelledReason cancelledReason) {
        this.cancelledReason = cancelledReason;
    }

    public Date getBusinessCreationTime() {
        return businessCreationTime;
    }

    public void setBusinessCreationTime(Date businessCreationTime) {
        this.businessCreationTime = businessCreationTime;
    }

    public boolean isOffseted() {
        return offseted;
    }

    public void setOffseted(boolean offseted) {
        this.offseted = offseted;
    }

    public Date getOffsetDate() {
        return offsetDate;
    }

    public void setOffsetDate(Date offsetDate) {
        this.offsetDate = offsetDate;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public boolean isPosted() {
        return posted;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getOffsetId() {
        return offsetId;
    }

    public void setOffsetId(String offsetId) {
        this.offsetId = offsetId;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public String getBusinessReferenceId() {
        return businessReferenceId;
    }

    public void setBusinessReferenceId(String businessReferenceId) {
        this.businessReferenceId = businessReferenceId;
    }


    public void copy(FeeCreatedEvent event) {
        setPrimaryKey(event.getFeeId().toString());
        setAmount(event.getAmount());
        setBusinessCreationTime(event.getBusinessCreationTime());
        setBusinessReferenceId(event.getBusinessReferenceId());
        setBusinessType(event.getBusinessType());
        setDueDate(event.getDueDate());
        setFeeStatus(event.getFeeStatus());
        setFeeType(event.getFeeType());
        setUserAccountId(event.getUserAccountId());
    }
}