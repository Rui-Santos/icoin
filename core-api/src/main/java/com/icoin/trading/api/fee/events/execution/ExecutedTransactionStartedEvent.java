package com.icoin.trading.api.fee.events.execution;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
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
 * Time: AM1:16
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedTransactionStartedEvent<T extends ExecutedTransactionStartedEvent> extends EventSupport<T> {
    protected final FeeTransactionId feeTransactionId;
    protected final OffsetId offsetId;
    protected final String orderId;
    protected final TransactionId orderTransactionId;
    protected final PortfolioId portfolioId;
    protected final Date tradeTime;
    protected final Date dueDate;
    protected final TradeType tradeType;
    protected final BigMoney tradedPrice;
    protected final BigMoney tradeAmount;
    protected final BigMoney executedMoney;
    protected final OrderBookId orderBookId;
    protected final CoinId coinId;

    public ExecutedTransactionStartedEvent(FeeTransactionId feeTransactionId,
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

        this.feeTransactionId = feeTransactionId;
        this.offsetId = offsetId;
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

    public OffsetId getOffsetId() {
        return offsetId;
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