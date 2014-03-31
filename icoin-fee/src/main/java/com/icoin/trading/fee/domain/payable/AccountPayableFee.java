package com.icoin.trading.fee.domain.payable;

import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeOffsetedEvent;
import com.icoin.trading.fee.domain.fee.FeeAggregateRoot;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:27
 * To change this template use File | Settings | File Templates.
 */
public class AccountPayableFee extends FeeAggregateRoot<AccountPayableFee> {
    @SuppressWarnings("UnusedDeclaration")
    protected AccountPayableFee() {
    }

    public AccountPayableFee(FeeId feeId, FeeStatus feeStatus, BigMoney amount, FeeType feeType, Date dueDate, Date businessCreationTime, String userAccountId, BusinessType businessType, String businessReferenceId) {
        apply(new AccountPayableFeeCreatedEvent(feeId, feeStatus, amount, feeType, dueDate, businessCreationTime, userAccountId, businessType, businessReferenceId));
    }

    public void confirm(Date confirmedDate) {
        apply(new AccountPayableFeeConfirmedEvent(feeId, confirmedDate));
    }

    public void cancel(CancelledReason cancelReason, Date cancelledDate) {
        apply(new AccountPayableFeeCancelledEvent(feeId, cancelReason, cancelledDate));
    }

    public void offset(Date offsetDate) {
        apply(new AccountPayableFeeOffsetedEvent(feeId, offsetDate));
    }

    @EventHandler
    public void on(AccountPayableFeeCreatedEvent event) {
        onCreated(event);
    }

    @EventHandler
    public void on(AccountPayableFeeConfirmedEvent event) {
        onConfirmed(event);
    }

    @EventHandler
    public void on(AccountPayableFeeCancelledEvent event) {
        onCancelled(event);
    }

    @EventHandler
    public void on(AccountPayableFeeOffsetedEvent event) {
        onOffseted(event);
    }
}
