package com.icoin.trading.fee.saga;

import com.icoin.trading.api.fee.command.offset.CancelOffsetCommand;
import com.icoin.trading.api.fee.command.offset.OffsetFeesCommand;
import com.icoin.trading.api.fee.command.payable.ConfirmAccountPayableFeeCommand;
import com.icoin.trading.api.fee.command.payable.OffsetAccountPayableFeeCommand;
import com.icoin.trading.api.fee.command.paid.CancelPaidFeeCommand;
import com.icoin.trading.api.fee.command.paid.ConfirmPaidFeeCommand;
import com.icoin.trading.api.fee.command.paid.OffsetPaidFeeCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.CancelledReason;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeOffsetedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeOffsetedEvent;
import com.icoin.trading.api.fee.events.offset.FeesOffsetedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetAmountNotMatchedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCancelledEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCreatedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:01
 * To change this template use File | Settings | File Templates.
 */
public class PayTransactionFeeManagerSaga extends AbstractAnnotatedSaga {
    protected transient CommandGateway commandGateway;

    protected FeeTransactionId feeTransactionId;
    protected FeeId accountPayableId;
    protected FeeId paidFeeId;
    protected OffsetId offsetId;
    protected TransactionStatus accountPayableStatus = TransactionStatus.NEW;
    protected TransactionStatus paidFeeStatus = TransactionStatus.NEW;
    protected TransactionStatus offsetStatus = TransactionStatus.NEW;

    @SagaEventHandler(associationProperty = "feeId", keyName = "accountPayableId")
    public void onPayableCreated(final AccountPayableFeeCreatedEvent event) {
        accountPayableStatus = TransactionStatus.CREATED;

        commandGateway.send(new ConfirmAccountPayableFeeCommand(event.getFeeId(), event.getBusinessCreationTime()));
    }

    @SagaEventHandler(associationProperty = "feeId", keyName = "paidFeeId")
    public void onPaidCreated(final PaidFeeCreatedEvent event) {
        paidFeeStatus = TransactionStatus.CREATED;

        commandGateway.send(new ConfirmPaidFeeCommand(event.getFeeId(), event.getBusinessCreationTime()));
    }

    @SagaEventHandler(associationProperty = "offsetId")
    public void onOffsetCreated(final OffsetCreatedEvent event) {
        offsetStatus = TransactionStatus.CONFIRMED;

        commandGateway.send(new OffsetFeesCommand(offsetId, event.getStartedDate()));
    }

    @SagaEventHandler(associationProperty = "offsetId")
    public void onFeesOffseted(final FeesOffsetedEvent event) {
        offsetStatus = TransactionStatus.OFFSETED;

        offsetIfPossible(event.getOffsetedDate());
    }

    private void offsetIfPossible(Date offsetDate) {
        if (offsetStatus == TransactionStatus.OFFSETED
                && accountPayableStatus == TransactionStatus.CONFIRMED
                && paidFeeStatus == TransactionStatus.CONFIRMED) {
            commandGateway.send(new OffsetAccountPayableFeeCommand(accountPayableId, offsetDate));
            commandGateway.send(new OffsetPaidFeeCommand(paidFeeId, offsetDate));
        }
    }

    @SagaEventHandler(associationProperty = "feeId", keyName = "accountPayableId")
    public void onPayableConfirmed(final AccountPayableFeeConfirmedEvent event) {
        accountPayableStatus = TransactionStatus.CONFIRMED;

        offsetIfPossible(event.getConfirmedDate());
    }

    @SagaEventHandler(associationProperty = "feeId", keyName = "paidFeeId")
    public void onPaidConfirmed(final PaidFeeConfirmedEvent event) {
        paidFeeStatus = TransactionStatus.CONFIRMED;

        offsetIfPossible(event.getConfirmedDate());
    }

    @SagaEventHandler(associationProperty = "feeId", keyName = "accountPayableId")
    public void onPayableOffseted(final AccountPayableFeeOffsetedEvent event) {
        accountPayableStatus = TransactionStatus.OFFSETED;

        completeIfPossible();
    }

    @SagaEventHandler(associationProperty = "feeId", keyName = "paidFeeId")
    public void onPaidOffseted(final PaidFeeOffsetedEvent event) {
        paidFeeStatus = TransactionStatus.OFFSETED;

        completeIfPossible();
    }

    private void completeIfPossible() {
        if (offsetStatus == TransactionStatus.OFFSETED
                && accountPayableStatus == TransactionStatus.OFFSETED
                && paidFeeStatus == TransactionStatus.OFFSETED) {
            end();
        }

        if (offsetStatus == TransactionStatus.CANCELLED
                && accountPayableStatus == TransactionStatus.CANCELLED
                && paidFeeStatus == TransactionStatus.CANCELLED) {
            end();
        }
    }

    @SagaEventHandler(associationProperty = "offsetId")
    public void onOffsetAmountNotMatched(final OffsetAmountNotMatchedEvent event) {
        commandGateway.send(new CancelOffsetCommand(offsetId, CancelledReason.AMOUNT_NOT_MATCHED, event.getOffsetDate()));
    }

    @SagaEventHandler(associationProperty = "offsetId")
    public void onOffsetCancelled(final OffsetCancelledEvent event) {
        offsetStatus = TransactionStatus.CANCELLED;

        commandGateway.send(new CancelPaidFeeCommand(accountPayableId, com.icoin.trading.api.fee.domain.fee.CancelledReason.OFFSET_ERROR, event.getCancelledDate()));
        commandGateway.send(new CancelPaidFeeCommand(paidFeeId, com.icoin.trading.api.fee.domain.fee.CancelledReason.OFFSET_ERROR, event.getCancelledDate()));
    }

    @SagaEventHandler(associationProperty = "feeId", keyName = "accountPayableId")
    public void onPayableCancelled(final AccountPayableFeeCancelledEvent event) {
        accountPayableStatus = TransactionStatus.CANCELLED;

        completeIfPossible();
    }

    @SagaEventHandler(associationProperty = "feeId", keyName = "paidFeeId")
    public void onPaidCancelled(final PaidFeeCancelledEvent event) {
        paidFeeStatus = TransactionStatus.CANCELLED;

        completeIfPossible();
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }
}