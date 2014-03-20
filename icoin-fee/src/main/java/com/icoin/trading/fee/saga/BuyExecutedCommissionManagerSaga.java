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
import com.icoin.trading.api.fee.events.commission.BuyExecutedCommissionTransactionStartedEvent;
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
public class BuyExecutedCommissionManagerSaga extends ExecutedCommissionManagerSaga {
    private static transient Logger logger = LoggerFactory.getLogger(BuyExecutedCommissionManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "feeTransactionId")
    public void onTransactionStarted(final BuyExecutedCommissionTransactionStartedEvent event) {
        feeTransactionId = event.getFeeTransactionId();
        accountReceivableId = new FeeId();

        associateWith("accountReceivableId", accountReceivableId.toString());
        commandGateway.send(
                new CreateAccountReceivableFeeCommand(
                        feeTransactionId,
                        accountReceivableId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.BUY_COMMISSION,
                        BusinessType.BUY_COMMISSION,
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
                        FeeType.BUY_COMMISSION,
                        BusinessType.BUY_COMMISSION,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString(),
                        new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, event.getPortfolioId().toString())));

        offsetId = new OffsetId();
        associateWith("offsetId", offsetId.toString());
        commandGateway.send(
                new CreateOffsetCommand(
                        offsetId,
                        OffsetType.RECEIVED_AR,
                        event.getPortfolioId().toString(),
                        ImmutableList.of(new FeeItem(accountReceivableId.toString(), FeeItemType.AR, event.getCommissionAmount())),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, event.getCommissionAmount())),
                        event.getCommissionAmount(),
                        event.getTradeTime()));
    }
}
