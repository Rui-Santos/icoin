package com.icoin.trading.api.fee.events.commission;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
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
    private final BigMoney commissionAmount;
    private final String orderId;
    private final String orderTransactionId;
    private final String portfolioId;
    private final Date tradeTime;
    private final Date dueDate;
    private final TradeType tradeType;
    private final BigMoney tradedPrice;
    private final BigMoney tradeAmount;
    private final BigMoney executedMoney;
    private final String orderBookId;
    private final String coinId;

    public ExecutedCommissionTransactionStartedEvent(FeeTransactionId feeTransactionId, BigMoney commissionAmount, String orderId, String orderTransactionId, String portfolioId, Date tradeTime, Date dueDate, TradeType tradeType, BigMoney tradedPrice, BigMoney tradeAmount, BigMoney executedMoney, String orderBookId, String coinId) {
        this.feeTransactionId = feeTransactionId;
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

    public String getOrderTransactionId() {
        return orderTransactionId;
    }

    public String getPortfolioId() {
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

    public String getOrderBookId() {
        return orderBookId;
    }

    public String getCoinId() {
        return coinId;
    }
}
