package com.icoin.trading.fee.domain.fee;

import com.homhon.base.domain.Identity;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

import java.util.Date;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-22
 * Time: PM12:21
 * To change this template use File | Settings | File Templates.
 */
public abstract class FeeEntity<T extends FeeEntity> extends AxonAnnotatedAggregateRoot<T, String> {
    @Identity
    @AggregateIdentifier
    private FeeId feeId;

    private AbstractFee fee;

    protected FeeEntity(AbstractFee fee) {
        notNull(fee);
        this.fee = fee;
    }

    public T addBusinessInfo(BusinessType businessType, String businessReferenceId) {
        notNull(businessType);
        hasLength(businessReferenceId);
        fee.setBusinessType(businessType);
        fee.setBusinessReferenceId(businessReferenceId);
        return (T) this;
    }

    public AbstractFee getFee() {
        return fee;
    }

    public BusinessType getBusinessType() {
        return fee.getBusinessType();
    }

    public String getBusinessReferenceId() {
        return fee.getBusinessReferenceId();
    }

    public FeeType getFeeType() {
        return fee.getFeeType();
    }

    public FeeStatus getFeeStatus() {
        return fee.getFeeStatus();
    }

    public Date getDueDate() {
        return fee.getDueDate();
    }

    public Date getBusinessCreationTime() {
        return fee.getBusinessCreationTime();
    }

    public Date getOffsetDate() {
        return fee.getOffsetDate();
    }

    public Date getPostDate() {
        return fee.getPostDate();
    }

    public Date getConfirmedDate() {
        return fee.getConfirmedDate();
    }

    public Date getCancelledDate() {
        return fee.getCancelledDate();
    }

    public boolean isOffseted() {
        return fee.isOffseted();
    }

    public BigMoney getAmount() {
        return fee.getAmount();
    }

    public String getUserAccountId() {
        return fee.getUserAccountId();
    }

    public void confirm(Date date) {
        fee.setFeeStatus(FeeStatus.CONFIRMED);
        fee.setConfirmedDate(date);
    }

    public CancelReason getCancelReason() {
        return fee.getCancelReason();
    }

    public FeeId getFeeId() {
        return feeId;
    }

    public void setFeeId(FeeId feeId) {
        this.feeId = feeId;
    }

    private void setFee(AbstractFee fee) {
        this.fee = fee;
    }

    public void cancel(Date date, CancelReason cancelReason) {
        fee.setFeeStatus(FeeStatus.CANCELLED);
        fee.setCancelledDate(date);
        fee.setCancelReason(cancelReason);
    }

    public void offset(Date date) {
        fee.setOffseted(true);
        fee.setOffsetDate(date);
    }
}
