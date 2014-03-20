package com.icoin.trading.tradeengine.domain.model.admin;

import com.icoin.trading.api.tradeengine.domain.ChangedReason;

import java.util.Date;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.TimeUtils.negativeInfinityTime;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM1:17
 * To change this template use File | Settings | File Templates.
 */
public class TradingSystemStatusAudit {
    private Date allowedToTradeStartTime;
    private String lastChangedBy;
    private Date lastChangedTime;
    private ChangedReason reason;

    public TradingSystemStatusAudit(Date allowedToTradeStartTime, String lastChangedBy, Date lastChangedTime, ChangedReason reason) {
        notNull(allowedToTradeStartTime);
        hasLength(lastChangedBy);
        notNull(reason);
        notNull(lastChangedTime);

        this.allowedToTradeStartTime = allowedToTradeStartTime;
        this.lastChangedBy = lastChangedBy;
        this.lastChangedTime = lastChangedTime;
        this.reason = reason;
    }

    public Date getAllowedToTradeStartTime() {
        return allowedToTradeStartTime;
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
}
