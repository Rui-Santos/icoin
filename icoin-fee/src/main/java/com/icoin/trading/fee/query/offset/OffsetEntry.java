package com.icoin.trading.fee.query.offset;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.OffsetStatus;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import org.joda.money.BigMoney;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:09
 * To change this template use File | Settings | File Templates.
 */
public class OffsetEntry extends VersionedEntitySupport<OffsetEntry, String, Integer> {
    private BigMoney offsetAmount;

    private String accountId;
    private List<FeeItem> arapList;
    private List<FeeItem> receivedPaidList;
    private OffsetType offsetType;
    private OffsetStatus offsetStatus = OffsetStatus.NOT_OFFSETED;
    private Date offsetDate;
    private Date cancelledDate;

    public BigMoney getOffsetAmount() {
        return offsetAmount;
    }

    public void setOffsetAmount(BigMoney offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<FeeItem> getArapList() {
        return arapList;
    }

    public void setArapList(List<FeeItem> arapList) {
        this.arapList = arapList;
    }

    public List<FeeItem> getReceivedPaidList() {
        return receivedPaidList;
    }

    public void setReceivedPaidList(List<FeeItem> receivedPaidList) {
        this.receivedPaidList = receivedPaidList;
    }

    public OffsetType getOffsetType() {
        return offsetType;
    }

    public void setOffsetType(OffsetType offsetType) {
        this.offsetType = offsetType;
    }

    public OffsetStatus getOffsetStatus() {
        return offsetStatus;
    }

    public void setOffsetStatus(OffsetStatus offsetStatus) {
        this.offsetStatus = offsetStatus;
    }

    public Date getOffsetDate() {
        return offsetDate;
    }

    public void setOffsetDate(Date offsetDate) {
        this.offsetDate = offsetDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }
}