package com.icoin.trading.fee.saga;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
import com.icoin.trading.api.fee.command.offset.OffsetFeesCommand;
import com.icoin.trading.api.fee.command.receivable.ConfirmAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.CreateAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.OffsetAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.received.ConfirmReceivedFeeCommand;
import com.icoin.trading.api.fee.command.received.CreateReceivedFeeCommand;
import com.icoin.trading.api.fee.command.received.OffsetReceivedFeeCommand;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.FeeItemType;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.domain.received.ReceivedSourceType;
import com.icoin.trading.api.fee.events.commission.SellExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeOffsetedEvent;
import com.icoin.trading.api.fee.events.fee.ReceivedFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.ReceivedFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.ReceivedFeeOffsetedEvent;
import com.icoin.trading.api.fee.events.offset.FeesOffsetedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCreatedEvent;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/18/14
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SellExecutedCommissionManagerSaga extends ExecutedCommissionManagerSaga {
    private static transient Logger logger = LoggerFactory.getLogger(SellExecutedCommissionManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "feeTransactionId")
    public void onTransactionStarted(final SellExecutedCommissionTransactionStartedEvent event) {
        feeTransactionId = event.getFeeTransactionId();
        accountReceivableId = new FeeId();

        associateWith("accountReceivableId", accountReceivableId.toString());
        commandGateway.send(
                new CreateAccountReceivableFeeCommand(
                        feeTransactionId,
                        accountReceivableId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.SELL_COMMISSION,
                        BusinessType.SELL_COMMISSION,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString()));

        receivedFeeId = new FeeId();

        associateWith("receivedFeeId", accountReceivableId.toString());
        commandGateway.send(
                new CreateReceivedFeeCommand(
                        feeTransactionId,
                        receivedFeeId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.SELL_COMMISSION,
                        BusinessType.SELL_COMMISSION,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, event.getPortfolioId())));

        offsetId = new OffsetId();
        associateWith("offsetId", offsetId.toString());
        commandGateway.send(
                new CreateOffsetCommand(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        event.getPortfolioId(),
                        ImmutableList.of(new FeeItem(accountReceivableId.toString(), FeeItemType.AR, event.getCommissionAmount())),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, event.getCommissionAmount())),
                        event.getCommissionAmount(),
                        event.getTradeTime()));
    }

//    @SagaEventHandler(associationProperty = "feeId", keyName = "accountReceivableId")
//    public void onReceivableCreated(final AccountReceivableFeeCreatedEvent event) {
//        accountReceivableStatus = TransactionStatus.CREATED;
//
//        commandGateway.send(new ConfirmAccountReceivableFeeCommand(event.getFeeId(), event.getBusinessCreationTime()));
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "receivedFeeId")
//    public void onReceivedCreated(final ReceivedFeeCreatedEvent event) {
//        receivedFeeStatus = TransactionStatus.CREATED;
//
//        commandGateway.send(new ConfirmReceivedFeeCommand(event.getFeeId(), event.getBusinessCreationTime()));
//    }
//
//    @SagaEventHandler(associationProperty = "offsetId")
//    public void onOffsetCreated(final OffsetCreatedEvent event) {
//        offsetStatus = TransactionStatus.CONFIRMED;
//
//        commandGateway.send(new OffsetFeesCommand(offsetId, event.getStartedDate()));
//    }
//
//    @SagaEventHandler(associationProperty = "offsetId")
//    public void onFeesOffseted(final FeesOffsetedEvent event) {
//        offsetStatus = TransactionStatus.OFFSETED;
//
//        offsetIfPossible(event.getOffsetedDate());
//    }
//
//    private void offsetIfPossible(Date offsetDate) {
//        if (offsetStatus == TransactionStatus.OFFSETED
//                && accountReceivableStatus == TransactionStatus.CONFIRMED
//                && receivedFeeStatus == TransactionStatus.CONFIRMED) {
//            commandGateway.send(new OffsetAccountReceivableFeeCommand(accountReceivableId, offsetDate));
//            commandGateway.send(new OffsetReceivedFeeCommand(receivedFeeId, offsetDate));
//        }
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "accountReceivableId")
//    public void onReceivableConfirmed(final AccountReceivableFeeConfirmedEvent event) {
//        accountReceivableStatus = TransactionStatus.CONFIRMED;
//
//        offsetIfPossible(event.getConfirmedDate());
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "receivedFeeId")
//    public void onReceivedConfirmed(final ReceivedFeeConfirmedEvent event) {
//        receivedFeeStatus = TransactionStatus.CONFIRMED;
//
//        offsetIfPossible(event.getConfirmedDate());
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "accountReceivableId")
//    public void onReceivableOffseted(final AccountReceivableFeeOffsetedEvent event) {
//        accountReceivableStatus = TransactionStatus.OFFSETED;
//
//        completeIfPossible();
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "receivedFeeId")
//    public void onReceivedOffseted(final ReceivedFeeOffsetedEvent event) {
//        receivedFeeStatus = TransactionStatus.OFFSETED;
//
//        completeIfPossible();
//    }
//
//    private void completeIfPossible() {
//        if (offsetStatus == TransactionStatus.OFFSETED
//                && accountReceivableStatus == TransactionStatus.OFFSETED
//                && receivedFeeStatus == TransactionStatus.OFFSETED){
//            end();
//        }
//    }
}
