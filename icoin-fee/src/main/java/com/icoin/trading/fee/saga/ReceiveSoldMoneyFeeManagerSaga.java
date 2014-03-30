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
import com.icoin.trading.api.fee.events.execution.ExecutedReceiveMoneyTransactionStartedEvent;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-30
 * Time: PM10:56
 * To change this template use File | Settings | File Templates.
 */
public class ReceiveSoldMoneyFeeManagerSaga extends ReceiveTransactionFeeManagerSaga {
    private static transient Logger logger = LoggerFactory.getLogger(PaySellCommissionFeeManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "feeTransactionId")
    public void onTransactionStarted(final ExecutedReceiveMoneyTransactionStartedEvent event) {
        logger.info("");
        feeTransactionId = event.getFeeTransactionId();
        accountReceivableId = event.getAccountReceivableFeeId();

        associateWith("accountReceivableId", accountReceivableId.toString());
        commandGateway.send(
                new CreateAccountReceivableFeeCommand(
                        feeTransactionId,
                        accountReceivableId,
                        FeeStatus.PENDING,
                        event.getExecutedMoney(),
                        FeeType.SOLD_MONEY,
                        BusinessType.TRADE_EXECUTED,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString()));

        receivedFeeId = event.getReceivedFeeId();

        associateWith("receivedFeeId", receivedFeeId.toString());
        commandGateway.send(
                new CreateReceivedFeeCommand(
                        feeTransactionId,
                        receivedFeeId,
                        FeeStatus.PENDING,
                        event.getExecutedMoney(),
                        FeeType.SOLD_MONEY,
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
                        ImmutableList.of(new FeeItem(accountReceivableId.toString(), FeeItemType.AR, event.getExecutedMoney())),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.RECEIVED, event.getExecutedMoney())),
                        event.getExecutedMoney(),
                        event.getTradeTime()));
    }
}
