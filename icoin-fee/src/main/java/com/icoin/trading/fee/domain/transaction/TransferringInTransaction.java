package com.icoin.trading.fee.domain.transaction;

import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.ExecutedFeeType;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.transfer.TransferTransactionType;
import com.icoin.trading.api.fee.domain.transfer.TransferType;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.transfer.in.TransferringInTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:13
 * To change this template use File | Settings | File Templates.
 */
public class TransferringInTransaction<T extends TransferringInTransaction> extends AxonAnnotatedAggregateRoot<T, String> {
    protected FeeTransactionId feeTransactionId;
    protected OffsetId offsetId;
    protected PortfolioId portfolioId;
    protected Date startTime;
    protected Date receivedDate;
    protected Date dueDate;
    protected FeeId receivedFeeId;
    protected FeeId accountReceivableFeeId;
    protected BigMoney amount;
    protected TransferTransactionType transactionType;
    protected TransferType transferType;

    protected void onStarted(TransferringInTransactionStartedEvent event) {
        this.feeTransactionId = event.getFeeTransactionId();
        this.offsetId = event.getOffsetId();
        this.portfolioId = event.getPortfolioId();
        this.startTime = event.getStartTime();
        this.dueDate = event.getDueDate();
        this.receivedFeeId = event.getReceivedFeeId();
        this.accountReceivableFeeId = event.getAccountReceivableFeeId();
        this.amount = event.getAmount();
        this.transactionType = event.getTransactionType();
        this.transferType = event.getTransferType();
    }

    @EventHandler
    public void on(ReceivedFeeConfirmedEvent event) {
        this.receivedDate = event.getConfirmedDate();
    }
}
