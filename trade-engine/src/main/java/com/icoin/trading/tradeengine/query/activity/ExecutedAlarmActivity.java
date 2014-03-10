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
        @CompoundIndex(name = "executedExceptionActivity_time_checked", def = "{'tradeTime': 1, 'checked': -1}", unique = true)
})
public class ExecutedAlarmActivity extends VersionedEntitySupport<ExecutedAlarmActivity, String, Long> {
    private String buyOrderId;
    private String sellOrderId;
    private String buyTransactionId;
    private String sellTransactionId;

    private BigMoney tradedAmount;
    private BigMoney tradedPrice;
    private BigMoney executedMoney;

    @Indexed
    private String coinId;
    @Indexed
    private String sellUsername;
    @Indexed
    private String buyUsername;
    @Indexed
    private String sellPortfolioId;
    @Indexed
    private String buyPortfolioId;
    @Indexed
    private String orderBookIdentifier;

    private Date tradeTime;
    private boolean checked;

    private TradeType tradeType;

    private ExecutedAlarmType type;

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

    public String getSellUsername() {
        return sellUsername;
    }

    public void setSellUsername(String sellUsername) {
        this.sellUsername = sellUsername;
    }

    public String getBuyUsername() {
        return buyUsername;
    }

    public void setBuyUsername(String buyUsername) {
        this.buyUsername = buyUsername;
    }

    public String getSellPortfolioId() {
        return sellPortfolioId;
    }

    public void setSellPortfolioId(String sellPortfolioId) {
        this.sellPortfolioId = sellPortfolioId;
    }

    public String getBuyPortfolioId() {
        return buyPortfolioId;
    }

    public void setBuyPortfolioId(String buyPortfolioId) {
        this.buyPortfolioId = buyPortfolioId;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public ExecutedAlarmType getType() {
        return type;
    }

    public void setType(ExecutedAlarmType type) {
        this.type = type;
    }
}