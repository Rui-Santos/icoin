package com.icoin.trading.fee.domain.transaction;

import com.homhon.base.domain.Identity;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.ExecutedFeeType;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.execution.ExecutedPayCoinTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.ExecutedReceiveCoinTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.ExecutedTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.users.domain.UserId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-3
 * Time: PM9:01
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCoinTransaction extends AxonAnnotatedAggregateRoot<ExecutedCoinTransaction, String> {

    @AggregateIdentifier
    @Identity
    private FeeTransactionId feeTransactionId;
    private String orderId;
    private BigMoney coinAmount;
    private TransactionId orderTransactionId;
    private PortfolioId portfolioId;
    private Date tradeTime;
    private Date dueDate;
    private FeeId receivedPaidFeeId;
    private FeeId receivablePayableFeeId;
    private ExecutedFeeType executedFeeType;

    @SuppressWarnings("UnusedDeclaration")
    protected ExecutedCoinTransaction() {
    }

    public ExecutedCoinTransaction(FeeTransactionId feeTransactionId,
                                   FeeId receivedPaidFeeId,
                                   FeeId receivablePayableFeeId,
                                   OffsetId offsetId,
                                   ExecutedFeeType executedFeeType,
                                   String orderId,
                                   TransactionId orderTransactionId,
                                   PortfolioId portfolioId,
                                   UserId userId,
                                   Date tradeTime,
                                   Date dueDate,
                                   TradeType tradeType,
                                   BigMoney tradedPrice,
                                   BigMoney tradeAmount,
                                   BigMoney executedMoney,
                                   OrderBookId orderBookId,
                                   CoinId coinId) {
        notNull(executedFeeType);

        switch (executedFeeType) {
            case PAY:
                apply(new ExecutedPayCoinTransactionStartedEvent(
                        feeTransactionId,
                        receivedPaidFeeId,
                        receivablePayableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        tradeType,
                        tradedPrice,
                        tradeAmount,
                        executedMoney,
                        orderBookId,
                        coinId));
                break;
            case RECEIVE:
                apply(new ExecutedReceiveCoinTransactionStartedEvent(
                        feeTransactionId,
                        receivedPaidFeeId,
                        receivablePayableFeeId,
                        offsetId,
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        userId,
                        tradeTime,
                        dueDate,
                        tradeType,
                        tradedPrice,
                        tradeAmount,
                        executedMoney,
                        orderBookId,
                        coinId));
                break;
        }
    }

    @EventHandler
    public void on(ExecutedPayCoinTransactionStartedEvent event) {
        onStart(event, ExecutedFeeType.PAY, event.getPaidFeeId(), event.getAccountPayableFeeId());
    }

    @EventHandler
    public void on(ExecutedReceiveCoinTransactionStartedEvent event) {
        onStart(event, ExecutedFeeType.RECEIVE, event.getReceivedFeeId(), event.getAccountReceivableFeeId());
    }

    private void onStart(ExecutedTransactionStartedEvent event, ExecutedFeeType type, FeeId receivedPaidFeeId, FeeId receivablePayableFeeId) {
        this.feeTransactionId = event.getFeeTransactionId();
        this.receivedPaidFeeId = receivedPaidFeeId;
        this.receivablePayableFeeId = receivablePayableFeeId;
        this.coinAmount = event.getTradeAmount();
        this.orderId = event.getOrderId();
        this.orderTransactionId = event.getOrderTransactionId();
        this.portfolioId = event.getPortfolioId();
        this.tradeTime = event.getTradeTime();
        this.dueDate = event.getDueDate();
        this.executedFeeType = type;
    }
}