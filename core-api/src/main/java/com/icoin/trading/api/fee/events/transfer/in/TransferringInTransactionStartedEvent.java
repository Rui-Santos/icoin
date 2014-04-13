package com.icoin.trading.api.fee.events.transfer.in;

import com.homhon.base.domain.event.EventSupport;
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
 * Time: PM9:11
 * To change this template use File | Settings | File Templates.
 */
public class TransferringInTransactionStartedEvent<T extends TransferringInTransactionStartedEvent> extends EventSupport<T> {
    protected final FeeTransactionId feeTransactionId;
    protected final OffsetId offsetId;
    protected final PortfolioId portfolioId;
    protected final UserId userId;
    protected final Date startTime;
    protected final Date dueDate;
    protected final FeeId receivedFeeId;
    protected final FeeId accountReceivableFeeId;
    protected final BigMoney amount;
    protected final TransferTransactionType transactionType;
    protected final TransferType transferType;
    protected final ReceivedSource receivedSource;
    protected final String receivedId;

    public TransferringInTransactionStartedEvent(FeeTransactionId feeTransactionId,
                                                 OffsetId offsetId,
                                                 PortfolioId portfolioId,
                                                 UserId userId,
                                                 Date startTime,
                                                 Date dueDate,
                                                 FeeId receivedFeeId,
                                                 FeeId accountReceivableFeeId,
                                                 BigMoney amount,
                                                 TransferTransactionType transactionType,
                                                 TransferType transferType,
                                                 ReceivedSource receivedSource,
                                                 String receivedId) {
        this.feeTransactionId = feeTransactionId;
        this.offsetId = offsetId;
        this.portfolioId = portfolioId;
        this.userId = userId;
        this.startTime = startTime;
        this.dueDate = dueDate;
        this.receivedFeeId = receivedFeeId;
        this.accountReceivableFeeId = accountReceivableFeeId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transferType = transferType;
        this.receivedSource = receivedSource;
        this.receivedId = receivedId;
    }

    public FeeTransactionId getFeeTransactionId() {
        return feeTransactionId;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public UserId getUserId() {
        return userId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public FeeId getReceivedFeeId() {
        return receivedFeeId;
    }

    public FeeId getAccountReceivableFeeId() {
        return accountReceivableFeeId;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public TransferTransactionType getTransactionType() {
        return transactionType;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public ReceivedSource getReceivedSource() {
        return receivedSource;
    }

    public String getReceivedId() {
        return receivedId;
    }
}
