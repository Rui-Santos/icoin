package com.icoin.trading.api.fee.events.fee.received;

import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.events.fee.FeeCreatedEvent;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:19
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedFeeCreatedEvent extends FeeCreatedEvent<ReceivedFeeCreatedEvent> {
    private final ReceivedSource receivedSource;

    public ReceivedFeeCreatedEvent(FeeId feeId,
                                   FeeStatus feeStatus,
                                   BigMoney amount,
                                   FeeType feeType,
                                   Date dueDate,
                                   Date businessCreationTime,
                                   String userAccountId,
                                   BusinessType businessType,
                                   String businessReferenceId,
                                   ReceivedSource receivedSource) {
        super(feeId, feeStatus, amount, feeType, dueDate, businessCreationTime, userAccountId, businessType, businessReferenceId);
        this.receivedSource = receivedSource;
    }

    public ReceivedSource getReceivedSource() {
        return receivedSource;
    }
}