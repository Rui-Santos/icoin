package com.icoin.trading.fee.domain.transaction;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.domain.transfer.TransferTransactionType;
import com.icoin.trading.api.fee.domain.transfer.TransferType;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.transfer.in.CoinTransferringInTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:22
 * To change this template use File | Settings | File Templates.
 */
public class CoinTransferringInTransaction extends TransferringInTransaction<CoinTransferringInTransaction> {
    private CoinId coinId;

    @SuppressWarnings("unused")
    protected CoinTransferringInTransaction() {
    }

    public CoinTransferringInTransaction(FeeTransactionId feeTransactionId,
                                         OffsetId offsetId,
                                         PortfolioId portfolioId,
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

        apply(new CoinTransferringInTransactionStartedEvent(feeTransactionId,
                offsetId,
                portfolioId,
                startTime,
                dueDate,
                receivedFeeId,
                accountReceivableFeeId,
                amount,
                coinId,
                transactionType,
                transferType,
                receivedSource,
                receivedId));

    }

    @EventHandler
    public void on(CoinTransferringInTransactionStartedEvent event) {
        onStarted(event);
        this.coinId = event.getCoinId();
    }


    public void received(Date receivedDate) {
        apply(new ReceivedFeeConfirmedEvent(receivedFeeId, receivedDate));
    }
}