package com.icoin.trading.model;

import org.joda.money.BigMoney;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement(name = "ExecutedTrade")
@XmlAccessorType(XmlAccessType.FIELD)
//@XmlJavaTypeAdapter(ExecutedTradeAdapter.class)
public class ExecutedTrade {
    private BigMoney tradeAmount;
    private BigMoney tradedPrice;
    private String buyOrderId;
    private String  coinId;
    private String sellOrderId;
    private String  buyTransactionId;
    private String  sellTransactionId;
    private String orderBookId;
    private Date tradeTime ;
    private TradeType tradeType;
    private BigMoney buyCommission;
    private BigMoney sellCommission;
    private BigMoney executedMoney;

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigMoney tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public void setTradedPrice(BigMoney tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(String sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public String getBuyTransactionId() {
        return buyTransactionId;
    }

    public void setBuyTransactionId(String buyTransactionId) {
        this.buyTransactionId = buyTransactionId;
    }

    public String getSellTransactionId() {
        return sellTransactionId;
    }

    public void setSellTransactionId(String sellTransactionId) {
        this.sellTransactionId = sellTransactionId;
    }

    public String getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(String orderBookId) {
        this.orderBookId = orderBookId;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public BigMoney getBuyCommission() {
        return buyCommission;
    }

    public void setBuyCommission(BigMoney buyCommission) {
        this.buyCommission = buyCommission;
    }

    public BigMoney getSellCommission() {
        return sellCommission;
    }

    public void setSellCommission(BigMoney sellCommission) {
        this.sellCommission = sellCommission;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public void setExecutedMoney(BigMoney executedMoney) {
        this.executedMoney = executedMoney;
    }
}