package com.icoin.trading.fee.saga;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
import com.icoin.trading.api.fee.command.paid.CreatePaidFeeCommand;
import com.icoin.trading.api.fee.command.payable.CreateAccountPayableFeeCommand;
import com.icoin.trading.api.fee.domain.PaidMode;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.FeeItemType;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import com.icoin.trading.api.fee.events.execution.ExecutedPayMoneyTransactionStartedEvent;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-31
 * Time: PM8:47
 * To change this template use File | Settings | File Templates.
 */
public class PaySellMoneyFeeManagerSaga extends PayTransactionFeeManagerSaga {
    private static transient Logger logger = LoggerFactory.getLogger(PaySellMoneyFeeManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "feeTransactionId")
    public void onTransactionStarted(final ExecutedPayMoneyTransactionStartedEvent event) {
        feeTransactionId = event.getFeeTransactionId();
        accountPayableId = event.getAccountPayableFeeId();

        associateWith("accountPayableId", accountPayableId.toString());
        commandGateway.send(
                new CreateAccountPayableFeeCommand(
                        feeTransactionId,
                        accountPayableId,
                        FeeStatus.PENDING,
                        event.getExecutedMoney(),
                        FeeType.PAY_MONEY,
                        BusinessType.TRADE_EXECUTED,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getUserId().toString(),
                        event.getOrderTransactionId().toString()));

        paidFeeId = event.getPaidFeeId();

        associateWith("paidFeeId", paidFeeId.toString());
        commandGateway.send(
                new CreatePaidFeeCommand(
                        feeTransactionId,
                        paidFeeId,
                        FeeStatus.PENDING,
                        event.getExecutedMoney(),
                        FeeType.PAY_MONEY,
                        BusinessType.TRADE_EXECUTED,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getUserId().toString(),
                        event.getOrderTransactionId().toString(),
                        PaidMode.INTERNAL));

        offsetId = event.getOffsetId();
        associateWith("offsetId", offsetId.toString());
        commandGateway.send(
                new CreateOffsetCommand(
                        offsetId,
                        OffsetType.AP_PAID,
                        event.getPortfolioId().toString(),
                        ImmutableList.of(new FeeItem(accountPayableId.toString(), FeeItemType.AP, event.getExecutedMoney())),
                        ImmutableList.of(new FeeItem(paidFeeId.toString(), FeeItemType.PAID, event.getExecutedMoney())),
                        event.getExecutedMoney(),
                        event.getTradeTime()));
    }
}
