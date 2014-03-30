package com.icoin.trading.fee.saga;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
import com.icoin.trading.api.fee.command.payable.CreateAccountPayableFeeCommand;
import com.icoin.trading.api.fee.command.received.CreateReceivedFeeCommand;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.FeeItemType;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.domain.received.ReceivedSourceType;
import com.icoin.trading.api.fee.events.execution.SellExecutedCommissionTransactionStartedEvent;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/18/14
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaySellCoinFeeManagerSaga extends PayTransactionFeeManagerSaga {
    private static transient Logger logger = LoggerFactory.getLogger(PaySellCoinFeeManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "feeTransactionId")
    public void onTransactionStarted(final SellExecutedCommissionTransactionStartedEvent event) {
        feeTransactionId = event.getFeeTransactionId();
        accountPayableId = event.getAccountPayableFeeId();

        associateWith("accountPayableId", accountPayableId.toString());
        commandGateway.send(
                new CreateAccountPayableFeeCommand(
                        feeTransactionId,
                        accountPayableId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.SELL_COMMISSION,
                        BusinessType.TRADE_EXECUTED,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString()));

        paidFeeId = event.getPaidFeeId();

        associateWith("paidFeeId", paidFeeId.toString());
        commandGateway.send(
                new CreateReceivedFeeCommand(
                        feeTransactionId,
                        paidFeeId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.SELL_COMMISSION,
                        BusinessType.TRADE_EXECUTED,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, event.getPortfolioId().toString())));

        offsetId = event.getOffsetId();
        associateWith("offsetId", offsetId.toString());
        commandGateway.send(
                new CreateOffsetCommand(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        event.getPortfolioId().toString(),
                        ImmutableList.of(new FeeItem(accountPayableId.toString(), FeeItemType.AR, event.getCommissionAmount())),
                        ImmutableList.of(new FeeItem(paidFeeId.toString(), FeeItemType.RECEIVED, event.getCommissionAmount())),
                        event.getCommissionAmount(),
                        event.getTradeTime()));
    }

//    @SagaEventHandler(associationProperty = "feeId", keyName = "accountPayableId")
//    public void onReceivableCreated(final AccountReceivableFeeCreatedEvent event) {
//        accountReceivableStatus = TransactionStatus.CREATED;
//
//        commandGateway.send(new ConfirmAccountReceivableFeeCommand(event.getFeeId(), event.getBusinessCreationTime()));
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "paidFeeId")
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
//            commandGateway.send(new OffsetAccountPayableFeeCommand(accountPayableId, offsetDate));
//            commandGateway.send(new OffsetReceivedFeeCommand(paidFeeId, offsetDate));
//        }
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "accountPayableId")
//    public void onReceivableConfirmed(final AccountReceivableFeeConfirmedEvent event) {
//        accountReceivableStatus = TransactionStatus.CONFIRMED;
//
//        offsetIfPossible(event.getConfirmedDate());
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "paidFeeId")
//    public void onReceivedConfirmed(final ReceivedFeeConfirmedEvent event) {
//        receivedFeeStatus = TransactionStatus.CONFIRMED;
//
//        offsetIfPossible(event.getConfirmedDate());
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "accountPayableId")
//    public void onReceivableOffseted(final AccountReceivableFeeOffsetedEvent event) {
//        accountReceivableStatus = TransactionStatus.OFFSETED;
//
//        completeIfPossible();
//    }
//
//    @SagaEventHandler(associationProperty = "feeId", keyName = "paidFeeId")
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
