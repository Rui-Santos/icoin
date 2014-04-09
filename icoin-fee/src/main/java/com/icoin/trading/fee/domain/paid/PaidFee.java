package com.icoin.trading.fee.domain.paid;

import com.icoin.trading.api.fee.domain.PaidMode;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeOffsetedEvent;
import com.icoin.trading.fee.domain.fee.FeeAggregateRoot;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:24
 * To change this template use File | Settings | File Templates.
 */
public class PaidFee extends FeeAggregateRoot<PaidFee> {
    private PaidMode paidMode;
    private String sequenceNumber;

    @SuppressWarnings("UnusedDeclaration")
    protected PaidFee() {

    }

    public PaidFee(FeeId feeId,
                   FeeStatus feeStatus,
                   BigMoney amount,
                   FeeType feeType,
                   Date dueDate,
                   Date businessCreationTime,
                   String userAccountId,
                   BusinessType businessType,
                   String businessReferenceId,
                   PaidMode paidSource) {
        apply(new PaidFeeCreatedEvent(feeId,
                feeStatus,
                amount,
                feeType,
                dueDate,
                businessCreationTime,
                userAccountId,
                businessType,
                businessReferenceId,
                paidSource));
    }

    public void confirm(String sequenceNumber,Date confirmedDate) {
        apply(new PaidFeeConfirmedEvent(feeId, sequenceNumber, confirmedDate));
    }

    public void cancel(CancelledReason cancelReason, Date cancelledDate) {
        apply(new PaidFeeCancelledEvent(feeId, cancelReason, cancelledDate));
    }

    public void offset(Date offsetDate) {
        apply(new PaidFeeOffsetedEvent(feeId, offsetDate));
    }

    @EventHandler
    public void on(PaidFeeCreatedEvent event) {
        onCreated(event);
        paidMode = event.getPaidMode();
    }

    @EventHandler
    public void on(PaidFeeConfirmedEvent event) {
        onConfirmed(event);
        this.sequenceNumber = event.getSequenceNumber();
    }

    @EventHandler
    public void on(PaidFeeCancelledEvent event) {
        onCancelled(event);
    }

    @EventHandler
    public void on(PaidFeeOffsetedEvent event) {
        onOffseted(event);
    }
}
