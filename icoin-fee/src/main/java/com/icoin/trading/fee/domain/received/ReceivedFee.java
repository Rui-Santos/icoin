package com.icoin.trading.fee.domain.received;

import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeOffsetedEvent;
import com.icoin.trading.fee.domain.fee.FeeAggregateRoot;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 4/10/13
 * Time: 3:14 PM
 */
public class ReceivedFee extends FeeAggregateRoot<ReceivedFee> {
    private ReceivedSource receivedSource;

    @SuppressWarnings("UnusedDeclaration")
    protected ReceivedFee(){

    }

    public ReceivedFee(FeeId feeId,
                       FeeStatus feeStatus,
                       BigMoney amount,
                       FeeType feeType,
                       Date dueDate,
                       Date businessCreationTime,
                       String userAccountId,
                       BusinessType businessType,
                       String businessReferenceId,
                       ReceivedSource receivedSource) {
        apply(new ReceivedFeeCreatedEvent(feeId,
                feeStatus,
                amount,
                feeType,
                dueDate,
                businessCreationTime,
                userAccountId,
                businessType,
                businessReferenceId,
                receivedSource));
    }

    public void confirm(Date confirmedDate) {
        apply(new ReceivedFeeConfirmedEvent(feeId, confirmedDate));
    }

    public void cancel(CancelledReason cancelReason, Date cancelledDate) {
        apply(new ReceivedFeeCancelledEvent(feeId, cancelReason, cancelledDate));
    }

    public void offset(Date offsetDate) {
        apply(new ReceivedFeeOffsetedEvent(feeId, offsetDate));
    }

    @EventHandler
    public void on(ReceivedFeeCreatedEvent event) {
        onCreated(event);
    }

    @EventHandler
    public void on(ReceivedFeeConfirmedEvent event) {
        onConfirmed(event);
    }

    @EventHandler
    public void on(ReceivedFeeCancelledEvent event) {
        onCancelled(event);
    }

    @EventHandler
    public void on(ReceivedFeeOffsetedEvent event) {
        onOffseted(event);
    }

}