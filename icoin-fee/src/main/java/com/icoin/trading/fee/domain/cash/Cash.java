package com.icoin.trading.fee.domain.cash;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM8:57
 * To change this template use File | Settings | File Templates.
 */
public abstract class Cash<T extends Cash> extends VersionedEntitySupport<T, String, Integer> {
    private BigMoney amount;
    private Date dueDate;
    private Date executedDueTime;
    private String userId;
    private CashStatus status;
    private boolean approved;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CashStatus getStatus() {
        return status;
    }

    public void setStatus(CashStatus status) {
        this.status = status;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public void setAmount(BigMoney amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getExecutedDueTime() {
        return executedDueTime;
    }

    public void setExecutedDueTime(Date executedDueTime) {
        this.executedDueTime = executedDueTime;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
