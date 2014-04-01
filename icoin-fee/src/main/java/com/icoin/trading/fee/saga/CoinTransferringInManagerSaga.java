package com.icoin.trading.fee.saga;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
import com.icoin.trading.api.fee.command.receivable.CreateAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.received.CreateReceivedFeeCommand;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.FeeItemType;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.domain.received.ReceivedSourceType;
import com.icoin.trading.api.fee.events.transfer.in.TransferringInTransactionStartedEvent;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:09
 * To change this template use File | Settings | File Templates.
 */
public class CoinTransferringInManagerSaga extends ReceiveTransactionFeeManagerSaga {
    private static transient Logger logger = LoggerFactory.getLogger(PaySellCommissionFeeManagerSaga.class);


    @StartSaga
    @SagaEventHandler(associationProperty = "feeTransactionId")
    public void onTransactionStarted(final TransferringInTransactionStartedEvent event) {
        logger.info("An new adding coins action transaction started", event);
        feeTransactionId = event.getFeeTransactionId();
        accountReceivableId = event.getAccountReceivableFeeId();

        associateWith("accountReceivableId", accountReceivableId.toString());
        commandGateway.send(
                new CreateAccountReceivableFeeCommand(
                        feeTransactionId,
                        accountReceivableId,
                        FeeStatus.PENDING,
                        event.getAmount(),
                        FeeType.RESERVE_COIN,
                        BusinessType.RESERVE_COIN,
                        event.getStartTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getReceivedId()));

        receivedFeeId = event.getReceivedFeeId();

        associateWith("receivedFeeId", receivedFeeId.toString());
        commandGateway.send(
                new CreateReceivedFeeCommand(
                        feeTransactionId,
                        receivedFeeId,
                        FeeStatus.PENDING,
                        event.getAmount(),
                        FeeType.RESERVE_COIN,
                        BusinessType.RESERVE_COIN,
                        event.getStartTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getReceivedId(),
                        event.getReceivedSource()));

        offsetId = event.getOffsetId();
        associateWith("offsetId", offsetId.toString());
        commandGateway.send(
                new CreateOffsetCommand(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        event.getPortfolioId().toString(),
                        ImmutableList.of(new FeeItem(accountReceivableId.toString(), FeeItemType.AR, event.getAmount())),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, event.getAmount())),
                        event.getAmount(),
                        event.getStartTime()));
    }

}