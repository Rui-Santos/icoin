package com.icoin.trading.tradeengine.domain.model.admin;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.TimeUtils.currentTime;
import static com.homhon.util.TimeUtils.negativeInfinityTime;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/5/14
 * Time: 6:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradingSystemStatus extends VersionedEntitySupport<TradingSystemStatus, String, Long> {
    private Date allowedToTradeStartTime = negativeInfinityTime();
    private String lastChangedBy;
    private Date lastChangedTime;
    private ChangedReason reason;

    private List<TradingSystemStatusAudit> history = new ArrayList();

    private void setReason(ChangedReason reason) {
        this.reason = reason;
    }

    private void setLastChangedBy(String lastChangedBy) {
        this.lastChangedBy = lastChangedBy;
    }

    private void setLastChangedTime(Date lastChangedTime) {
        this.lastChangedTime = lastChangedTime;
    }

    private void setHistory(List<TradingSystemStatusAudit> history) {
        this.history = history;
    }

//    private boolean allowedToTradeCurrently() { 
//        return allowedToTrade(currentTime()); 
//    } 

    public boolean allowedToTrade(Date placeDate) {
        notNull(placeDate);
        return allowedToTradeStartTime != null && allowedToTradeStartTime.compareTo(placeDate) <= 0;
    }

    public TradingSystemStatus disableTrading(Date allowedToTradeStartTime, String changedBy, Date changedTime, ChangedReason reason) {
        notNull(allowedToTradeStartTime);
        hasLength(changedBy);
        notNull(reason);
        notNull(changedTime);

        history.add(dumpCurrentStatus());

        this.allowedToTradeStartTime = allowedToTradeStartTime;
        this.lastChangedBy = changedBy;
        this.lastChangedTime = changedTime;
        this.reason = reason;
        return this;
    }

    public TradingSystemStatus reviveTrading(String changedBy, Date changedTime, ChangedReason reason) {
        hasLength(changedBy);
        notNull(reason);
        notNull(changedTime);

        history.add(dumpCurrentStatus());

        allowedToTradeStartTime = currentTime();
        this.lastChangedBy = changedBy;
        this.lastChangedTime = changedTime;
        this.reason = reason;
        return this;
    }

    private TradingSystemStatusAudit dumpCurrentStatus() {
        return new TradingSystemStatusAudit(
                allowedToTradeStartTime,
                lastChangedBy,
                lastChangedTime,
                reason);
    }

    public String getLastChangedBy() {
        return lastChangedBy;
    }

    public Date getLastChangedTime() {
        return lastChangedTime;
    }

    public ChangedReason getReason() {
        return reason;
    }

    public List<TradingSystemStatusAudit> getHistory() {
        return history;
    }
} 