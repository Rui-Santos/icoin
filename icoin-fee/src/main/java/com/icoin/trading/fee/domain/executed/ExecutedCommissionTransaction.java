package com.icoin.trading.fee.domain.executed;

import com.homhon.base.domain.Identity;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM8:32
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionTransaction extends AxonAnnotatedAggregateRoot<ExecutedCommissionTransaction, String> {

    @AggregateIdentifier
    @Identity
    private String transactionId;
    private String userAccountId;
    private BusinessType businessType;
    private String businessReferenceId;
    private Date executedTime;
    private Date dueDate;
    private ReceivedSource receivedSource;
    private String orderBookIdentifier;

    private String coinId;

    private TradeType tradeType;

    private BigMoney amount;
    private FeeType feeType;

    private String receivedId;
    private String receivableId;


    @SuppressWarnings("UnusedDeclaration")
    protected ExecutedCommissionTransaction() {
    }

    public ExecutedCommissionTransaction(String transactionId,
                                         String userAccountId,
                                         BusinessType businessType,
                                         String businessReferenceId,
                                         BigMoney receiveMoney,
                                         Date executedTime,
                                         ReceivedSource receivedSource) {
        apply(new ReceiveTransactionStartedEvent(transactionId, userAccountId, businessType, businessReferenceId, receiveMoney, executedTime, receivedSource));
    }



    public String getTransactionId() {
        return transactionId;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public String getBusinessReferenceId() {
        return businessReferenceId;
    }



    public Date getExecutedTime() {
        return executedTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public ReceivedSource getReceivedSource() {
        return receivedSource;
    }

    public String getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public String getCoinId() {
        return coinId;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public String getReceivedId() {
        return receivedId;
    }

    public String getReceivableId() {
        return receivableId;
    }

    //    @EventHandler
//    public void on(ReceivedAddedEvent e) {
//        this.added = true;
//    }
}