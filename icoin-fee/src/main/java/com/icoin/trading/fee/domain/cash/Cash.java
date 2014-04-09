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
public abstract class Cash<T extends Cash> extends VersionedEntitySupport<T, String, Integer> implements SchedulableEntity<T, String> {
    protected BigMoney amount;
    protected Date confirmedDate;
    protected Date cancelledDate;
    protected Date dueDate;
    protected Date scheduledTime;
    protected String userId;
    protected String portfolioId;
    protected CashStatus status = CashStatus.CREATED;
    protected boolean approved;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
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

    private void setAmount(BigMoney amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date executedDueTime) {
        this.scheduledTime = executedDueTime;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Date getConfirmedDate() {
        return confirmedDate;
    }

    private void setConfirmedDate(Date confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public String getUserId() {
        return userId;
    }

    private void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void cancel(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
        this.status = CashStatus.CANCELLED;
    }


}
