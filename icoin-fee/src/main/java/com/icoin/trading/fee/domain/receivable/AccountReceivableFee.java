package com.icoin.trading.fee.domain.receivable;

import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeOffsetedEvent;
import com.icoin.trading.fee.domain.fee.FeeAggregateRoot;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:21
 * To change this template use File | Settings | File Templates.
 */
public class AccountReceivableFee extends FeeAggregateRoot<AccountReceivableFee> {
    public AccountReceivableFee(FeeId feeId, FeeStatus feeStatus, BigMoney amount, FeeType feeType, Date dueDate, Date businessCreationTime, String userAccountId, BusinessType businessType, String businessReferenceId) {
        apply(new AccountReceivableFeeCreatedEvent(feeId, feeStatus, amount, feeType, dueDate, businessCreationTime, userAccountId, businessType, businessReferenceId));
    }

    public void confirm(Date confirmedDate) {
        apply(new AccountReceivableFeeConfirmedEvent(feeId, confirmedDate));
    }

    public void cancel(CancelledReason cancelReason,Date cancelledDate) {
        apply(new AccountReceivableFeeCancelledEvent(feeId, cancelReason, cancelledDate));
    }

    public void offset(Date offsetDate) {
        apply(new AccountReceivableFeeOffsetedEvent(feeId, offsetDate));
    }

    @EventHandler
    public void on(AccountReceivableFeeConfirmedEvent event) {
        onConfirm(event);
    }

    @EventHandler
    public void on(AccountReceivableFeeCancelledEvent event) {
        onCancel(event);
    }

    @EventHandler
    public void on(AccountReceivableFeeOffsetedEvent event) {
        onOffset(event);
    }
}