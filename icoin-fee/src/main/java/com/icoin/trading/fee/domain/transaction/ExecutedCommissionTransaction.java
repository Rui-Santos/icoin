package com.icoin.trading.fee.domain.transaction;

import com.homhon.base.domain.Identity;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.ExecutedFeeType;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.execution.BuyExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.ExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.fee.events.execution.SellExecutedCommissionTransactionStartedEvent;
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
public class ExecutedCommissionTransaction extends AxonAnnotatedAggregateRoot<ExecutedCommissionTransaction, String> {

    @AggregateIdentifier
    @Identity
    private FeeTransactionId feeTransactionId;
    private BigMoney commissionAmount;
    private String orderId;
    private TransactionId orderTransactionId;
    private PortfolioId portfolioId;
    private Date tradeTime;
    private Date dueDate;
    private ExecutedFeeType executedFeeType;

    @SuppressWarnings("UnusedDeclaration")
    protected ExecutedCommissionTransaction() {
    }

    public ExecutedCommissionTransaction(FeeTransactionId feeTransactionId,
                                         FeeId receivedFeeId,
                                         FeeId accountReceivableFeeId,
                                         OffsetId offsetId,
                                         ExecutedFeeType executedFeeType,
                                         BigMoney commissionAmount,
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
            case SELL_COMMISSION:
                apply(new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
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
            case BUY_COMMISSION:
                apply(new BuyExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        commissionAmount,
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
    public void on(SellExecutedCommissionTransactionStartedEvent event) {
        onStart(event, ExecutedFeeType.SELL_COMMISSION);
    }

    @EventHandler
    public void on(BuyExecutedCommissionTransactionStartedEvent event) {
        onStart(event, ExecutedFeeType.BUY_COMMISSION);
    }

    private void onStart(ExecutedCommissionTransactionStartedEvent event, ExecutedFeeType type) {
        feeTransactionId = event.getFeeTransactionId();
        commissionAmount = event.getCommissionAmount();
        orderId = event.getOrderId();
        orderTransactionId = event.getOrderTransactionId();
        portfolioId = event.getPortfolioId();
        tradeTime = event.getTradeTime();
        dueDate = event.getDueDate();
        executedFeeType = type;
    }
}