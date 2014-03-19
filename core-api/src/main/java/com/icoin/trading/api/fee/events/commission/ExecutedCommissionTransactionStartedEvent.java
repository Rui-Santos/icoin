package com.icoin.trading.api.fee.events.commission;

import com.homhon.base.domain.event.EventSupport;
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
 * Date: 14-3-18
 * Time: PM9:35
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionTransactionStartedEvent<T extends ExecutedCommissionTransactionStartedEvent> extends EventSupport<T> {


    private final FeeTransactionId feeTransactionId;
    private final FeeId receivedFeeId;
    private final FeeId accountReceivableFeeId;
    private final OffsetId offsetId;
    private final BigMoney commissionAmount;
    private final String orderId;
    private final TransactionId orderTransactionId;
    private final PortfolioId portfolioId;
    private final Date tradeTime;
    private final Date dueDate;
    private final TradeType tradeType;
    private final BigMoney tradedPrice;
    private final BigMoney tradeAmount;
    private final BigMoney executedMoney;
    private final OrderBookId orderBookId;
    private final CoinId coinId;

    public ExecutedCommissionTransactionStartedEvent(FeeTransactionId feeTransactionId,
                                                     FeeId receivedFeeId,
                                                     FeeId accountReceivableFeeId,
                                                     OffsetId offsetId,
                                                     BigMoney commissionAmount,
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

        this.feeTransactionId = feeTransactionId;
        this.receivedFeeId = receivedFeeId;
        this.accountReceivableFeeId = accountReceivableFeeId;
        this.offsetId = offsetId;
        this.commissionAmount = commissionAmount;
        this.orderId = orderId;
        this.orderTransactionId = orderTransactionId;
        this.portfolioId = portfolioId;
        this.tradeTime = tradeTime;
        this.dueDate = dueDate;
        this.tradeType = tradeType;
        this.tradedPrice = tradedPrice;
        this.tradeAmount = tradeAmount;
        this.executedMoney = executedMoney;
        this.orderBookId = orderBookId;
        this.coinId = coinId;
    }

    public FeeTransactionId getFeeTransactionId() {
        return feeTransactionId;
    }

    public BigMoney getCommissionAmount() {
        return commissionAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public TransactionId getOrderTransactionId() {
        return orderTransactionId;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public FeeId getReceivedFeeId() {
        return receivedFeeId;
    }

    public FeeId getAccountReceivableFeeId() {
        return accountReceivableFeeId;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public CoinId getCoinId() {
        return coinId;
    }
}
