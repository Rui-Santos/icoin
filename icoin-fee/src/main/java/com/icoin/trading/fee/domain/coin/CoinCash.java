package com.icoin.trading.fee.domain.coin;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.fee.domain.address.Address;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM8:57
 * To change this template use File | Settings | File Templates.
 */
public class CoinCash extends VersionedEntitySupport<CoinCash, String, Integer> {
    private BigMoney amount;
    private Date dueDate;
    private Date executedDueTime;
    private String userId;
    private CoinStatus status;
    private Address address;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CoinStatus getStatus() {
        return status;
    }

    public void setStatus(CoinStatus status) {
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
}
