package com.icoin.trading.fee.domain.fee;

import com.homhon.base.domain.Identity;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.events.fee.FeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.FeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.FeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.FeeOffsetedEvent;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-22
 * Time: PM12:21
 * To change this template use File | Settings | File Templates.
 */
public abstract class FeeAggregateRoot<T extends FeeAggregateRoot> extends AxonAnnotatedAggregateRoot<T, String> {
    @AggregateIdentifier
    @Identity
    protected FeeId feeId;

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


    protected void onCreated(FeeCreatedEvent event) {
        this.feeId = event.getFeeId();
        this.feeStatus = event.getFeeStatus();
        this.amount = event.getAmount();
        this.feeType = event.getFeeType();
        this.dueDate = event.getDueDate();
        this.businessCreationTime = event.getBusinessCreationTime();
        this.userAccountId = event.getUserAccountId();
        this.businessType = event.getBusinessType();
        this.businessReferenceId = event.getBusinessReferenceId();
    }

    protected void onConfirmed(FeeConfirmedEvent event) {
        this.feeStatus = FeeStatus.CONFIRMED;
        this.confirmedDate = event.getConfirmedDate();
    }

    protected void onCancelled(FeeCancelledEvent event) {
        this.feeStatus = FeeStatus.CANCELLED;
        this.cancelledDate = event.getCancelledDate();
        this.cancelledReason = event.getCancelledReason();
    }

    protected void onOffseted(FeeOffsetedEvent event) {
        this.offseted = true;
        this.offsetDate = event.getOffsetedDate();
    }

    public boolean isCancelled() {
        return FeeStatus.CANCELLED == feeStatus;
    }

    public boolean isConfirmed() {
        return FeeStatus.CONFIRMED == feeStatus;
    }

    public boolean isPending() {
        return FeeStatus.PENDING == feeStatus;
    }

    public boolean isPosted() {
        return posted;
    }

    public boolean isOffseted() {
        return offseted;
    }

    public boolean isComplete() {
        return isConfirmed() && isOffseted();
    }

    public boolean isFinished() {
        return isComplete() || isCancelled();
    }

    public FeeStatus getFeeStatus() {
        return feeStatus;
    }
}
