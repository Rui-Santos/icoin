package com.icoin.trading.fee.domain.receivable;

import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeCreatedEvent;
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

    @EventHandler
    public void on(AccountReceivableFeeCreatedEvent event) {
        onCreated(event);
    }
}