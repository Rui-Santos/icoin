package com.icoin.trading.api.fee.events.transfer.in;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.domain.transfer.TransferTransactionType;
import com.icoin.trading.api.fee.domain.transfer.TransferType;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.users.domain.UserId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:12
 * To change this template use File | Settings | File Templates.
 */
public class CoinTransferringInTransactionStartedEvent extends TransferringInTransactionStartedEvent<CoinTransferringInTransactionStartedEvent> {
    protected final CoinId coinId;

    public CoinTransferringInTransactionStartedEvent(FeeTransactionId feeTransactionId,
                                                     OffsetId offsetId,
                                                     PortfolioId portfolioId,
                                                     UserId userId,
                                                     Date startTime,
                                                     Date dueDate,
                                                     FeeId receivedFeeId,
                                                     FeeId accountReceivableFeeId,
                                                     BigMoney amount,
                                                     CoinId coinId,
                                                     TransferTransactionType transactionType,
                                                     TransferType transferType,
                                                     ReceivedSource receivedSource,
                                                     String receivedId) {
        super(feeTransactionId,
                offsetId,
                portfolioId,
                userId,
                startTime,
                dueDate,
                receivedFeeId,
                accountReceivableFeeId,
                amount,
                transactionType,
                transferType,
                receivedSource,
                receivedId);
        this.coinId = coinId;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    @Override
    public String toString() {
        return "CoinTransferringInTransactionStartedEvent{" +
                "feeTransactionId=" + feeTransactionId +
                ", offsetId=" + offsetId +
                ", portfolioId=" + portfolioId +
                ", userId=" + userId +
                ", startTime=" + startTime +
                ", dueDate=" + dueDate +
                ", receivedFeeId=" + receivedFeeId +
                ", accountReceivableFeeId=" + accountReceivableFeeId +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", transferType=" + transferType +
                ", receivedSource=" + receivedSource +
                ", receivedId='" + receivedId + '\'' +
                '}';
    }
}