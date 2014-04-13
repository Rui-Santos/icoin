package com.icoin.trading.api.fee.events.execution;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.users.domain.UserId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/18/14
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuyExecutedCommissionTransactionStartedEvent extends ExecutedCommissionTransactionStartedEvent<BuyExecutedCommissionTransactionStartedEvent> {
    public BuyExecutedCommissionTransactionStartedEvent(FeeTransactionId feeTransactionId,
                                                        FeeId receivedFeeId,
                                                        FeeId accountReceivableFeeId,
                                                        OffsetId offsetId,
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
        super(feeTransactionId,
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
                coinId);
    }

    @Override
    public String toString() {
        return "BuyExecutedCommissionTransactionStartedEvent{" +
                "feeTransactionId=" + feeTransactionId +
                ", paidFeeId=" + paidFeeId +
                ", accountPayableFeeId=" + accountPayableFeeId +
                ", offsetId=" + offsetId +
                ", commissionAmount=" + commissionAmount +
                ", orderId='" + orderId + '\'' +
                ", orderTransactionId=" + orderTransactionId +
                ", portfolioId=" + portfolioId +
                ", tradeTime=" + tradeTime +
                ", dueDate=" + dueDate +
                ", tradeType=" + tradeType +
                ", tradedPrice=" + tradedPrice +
                ", tradeAmount=" + tradeAmount +
                ", executedMoney=" + executedMoney +
                ", orderBookId=" + orderBookId +
                ", coinId=" + coinId +
                '}';
    }
}