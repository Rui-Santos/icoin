package com.icoin.trading.api.fee.events.execution;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-29
 * Time: AM1:20
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedReceiveMoneyTransactionStartedEvent extends ExecutedTransactionStartedEvent<ExecutedReceiveMoneyTransactionStartedEvent> {
    protected final FeeId receivedFeeId;
    protected final FeeId accountReceivableFeeId;

    public ExecutedReceiveMoneyTransactionStartedEvent(FeeTransactionId feeTransactionId,
                                                       FeeId receivedFeeId,
                                                       FeeId accountReceivableFeeId,
                                                       OffsetId offsetId,
                                                       String orderId,
                                                       TransactionId orderTransactionId,
                                                       PortfolioId portfolioId,
                                                       Date tradeTime,
                                                       Date dueDate,
                                                       TradeType tradeType,
                                                       BigMoney tradedPrice,
                                                       BigMoney tradeAmount,
                                                       BigMoney executedMoney,
                                                       OrderBookId orderBookId,
                                                       CoinId coinId) {
        super(feeTransactionId,
                offsetId,
                orderId,
                orderTransactionId,
                portfolioId,
                tradeTime,
                dueDate,
                tradeType,
                tradedPrice,
                tradeAmount,
                executedMoney,
                orderBookId,
                coinId);

        this.receivedFeeId = receivedFeeId;
        this.accountReceivableFeeId = accountReceivableFeeId;
    }

    public FeeId getReceivedFeeId() {
        return receivedFeeId;
    }

    public FeeId getAccountReceivableFeeId() {
        return accountReceivableFeeId;
    }
}