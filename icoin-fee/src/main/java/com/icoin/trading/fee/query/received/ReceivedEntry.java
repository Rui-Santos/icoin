package com.icoin.trading.fee.query.received;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import org.joda.money.BigMoney;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 9/10/13
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
//@CompoundIndexes({
//        @CompoundIndex(name = "portfolioActivity_user_type", def = "{'username': 1, 'type': 1}", unique = true),
//        @CompoundIndex(name = "portfolioActivity_portfolio_type", def = "{'portfolioId': 1, 'type': 1}", unique = true)
//})
public class ReceivedEntry extends VersionedEntitySupport<ReceivedEntry, String, Integer> {

    @Indexed
    private String feeTransactionId;

    private ReceivedSource receivedSource;

    private String orderBookIdentifier;

    private String coinId;

    private TradeType tradeType;

    private BigMoney amount;
    private BusinessType businessType;

    @Indexed
    private Date createdTime;
    @Indexed
    private Date dueDate;
    @Indexed
    private String userAccountId;
    private String businessReferenceId;

    public String getFeeTransactionId() {
        return feeTransactionId;
    }

    public void setFeeTransactionId(String feeTransactionId) {
        this.feeTransactionId = feeTransactionId;
    }

    public ReceivedSource getReceivedSource() {
        return receivedSource;
    }

    public void setReceivedSource(ReceivedSource receivedSource) {
        this.receivedSource = receivedSource;
    }

    public String getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public void setOrderBookIdentifier(String orderBookIdentifier) {
        this.orderBookIdentifier = orderBookIdentifier;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public void setAmount(BigMoney amount) {
        this.amount = amount;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getBusinessReferenceId() {
        return businessReferenceId;
    }

    public void setBusinessReferenceId(String businessReferenceId) {
        this.businessReferenceId = businessReferenceId;
    }
}
