package com.icoin.trading.tradeengine.query.activity;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeType;
import org.joda.money.BigMoney;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/26/14
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
@CompoundIndexes({
        @CompoundIndex(name = "portfolioActivity_user_type", def = "{'username': 1, 'type': 1}", unique = true),
        @CompoundIndex(name = "portfolioActivity_portfolio_type", def = "{'portfolioId': 1, 'type': 1}", unique = true)
})
public class ExecutedExceptionActivity extends VersionedEntitySupport<ExecutedExceptionActivity, String, Long> {
    @Indexed
    private String buyOrderId;
    @Indexed
    private String sellOrderId;
    @Indexed
    private String buyTransactionId;
    @Indexed
    private String sellTransactionId;
    private BigMoney tradedAmount;
    private BigMoney tradedPrice;
    private BigMoney executedMoney;
    private String coinId;
    private String orderBookIdentifier;
    @Indexed
    private Date tradeTime;

    private TradeType tradeType;

    private ExecutedExceptionActivityType type;


    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
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

    public BigMoney getTradedAmount() {
        return tradedAmount;
    }

    public void setTradedAmount(BigMoney tradedAmount) {
        this.tradedAmount = tradedAmount;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public void setTradedPrice(BigMoney tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public void setExecutedMoney(BigMoney executedMoney) {
        this.executedMoney = executedMoney;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public void setOrderBookIdentifier(String orderBookIdentifier) {
        this.orderBookIdentifier = orderBookIdentifier;
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

    public ExecutedExceptionActivityType getType() {
        return type;
    }

    public void setType(ExecutedExceptionActivityType type) {
        this.type = type;
    }
}