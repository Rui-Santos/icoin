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
public class PayBuyCommissionFeeManagerSaga extends PayTransactionFeeManagerSaga {
    private static transient Logger logger = LoggerFactory.getLogger(PayBuyCommissionFeeManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "feeTransactionId")
    public void onTransactionStarted(final BuyExecutedCommissionTransactionStartedEvent event) {
        feeTransactionId = event.getFeeTransactionId();
        accountPayableId = event.getAccountPayableFeeId();

        associateWith("accountPayableId", accountPayableId.toString());
        commandGateway.send(
                new CreateAccountPayableFeeCommand(
                        feeTransactionId,
                        accountPayableId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.BOUGHT_MONEY_COMMISSION,
                        BusinessType.TRADE_EXECUTED,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString()));

        paidFeeId = event.getPaidFeeId();

        associateWith("paidFeeId", paidFeeId.toString());
        commandGateway.send(
                new CreatePaidFeeCommand(
                        feeTransactionId,
                        paidFeeId,
                        FeeStatus.PENDING,
                        event.getCommissionAmount(),
                        FeeType.BOUGHT_MONEY_COMMISSION,
                        BusinessType.TRADE_EXECUTED,
                        event.getTradeTime(),
                        event.getDueDate(),
                        event.getPortfolioId().toString(),
                        event.getOrderTransactionId().toString(),
                        PaidMode.INTERNAL));

        offsetId = event.getOffsetId();
        associateWith("offsetId", offsetId.toString());
        commandGateway.send(
                new CreateOffsetCommand(
                        offsetId,
                        OffsetType.AP_PAID,
                        event.getPortfolioId().toString(),
                        ImmutableList.of(new FeeItem(accountPayableId.toString(), FeeItemType.AP, event.getCommissionAmount())),
                        ImmutableList.of(new FeeItem(paidFeeId.toString(), FeeItemType.PAID, event.getCommissionAmount())),
                        event.getCommissionAmount(),
                        event.getTradeTime()));
    }
}
