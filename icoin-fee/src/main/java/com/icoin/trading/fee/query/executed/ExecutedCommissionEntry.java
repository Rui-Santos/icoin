package com.icoin.trading.fee.query.executed;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.api.fee.domain.CommissionType;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM8:26
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionEntry extends VersionedEntitySupport<ExecutedCommissionEntry, String, Integer> {
    private BigMoney commissionAmount;
    private String orderId;
    private String orderTransactionId;
    private String portfolioId;
    private Date tradeTime;
    private Date dueDate;
    private TradeType tradeType;
    private BigMoney tradedPrice;
    private CommissionType type;
    private BigMoney tradeAmount;
    private BigMoney executedMoney;
    private String orderBookId;
    private String coinId;

    public BigMoney getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigMoney commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTransactionId() {
        return orderTransactionId;
    }

    public void setOrderTransactionId(String orderTransactionId) {
        this.orderTransactionId = orderTransactionId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public void setTradedPrice(BigMoney tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public CommissionType getType() {
        return type;
    }

    public void setType(CommissionType type) {
        this.type = type;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigMoney tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public void setExecutedMoney(BigMoney executedMoney) {
        this.executedMoney = executedMoney;
    }

    public String getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(String orderBookId) {
        this.orderBookId = orderBookId;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }
}