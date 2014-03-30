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
import com.icoin.trading.api.fee.events.execution.BuyExecutedCommissionTransactionStartedEvent;
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
public class PayBuyCommissionFeeManagerSaga extends ReceiveTransactionFeeManagerSaga {
    private static transient Logger logger = LoggerFactory.getLogger(PayBuyCommissionFeeManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "feeTransactionId")
    public void onTransactionStarted(final BuyExecutedCommissionTransactionStartedEvent event) {
        feeTransactionId = event.getFeeTransactionId();
        accountReceivableId = event.getAccountPayableFeeId();

        associateWith("accountReceivableId", accountReceivableId.toString());
        commandGateway.send(
                new CreateAccountReceivableFeeCommand(
                        feeTransactionId,
                        accountReceivableId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.BUY_COMMISSION,
                        BusinessType.TRADE_EXECUTED,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString()));

        receivedFeeId = event.getPaidFeeId();

        associateWith("paidFeeId", receivedFeeId.toString());
        commandGateway.send(
                new CreateReceivedFeeCommand(
                        feeTransactionId,
                        receivedFeeId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.BUY_COMMISSION,
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
                        ImmutableList.of(new FeeItem(accountReceivableId.toString(), FeeItemType.AR, event.getCommissionAmount())),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, event.getCommissionAmount())),
                        event.getCommissionAmount(),
                        event.getTradeTime()));
    }
}
