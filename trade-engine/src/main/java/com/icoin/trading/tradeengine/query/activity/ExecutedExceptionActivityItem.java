package com.icoin.trading.tradeengine.query.activity;

import com.homhon.base.domain.model.ValueObjectSupport;
import org.joda.money.BigMoney;
import org.joda.time.LocalDateTime;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/26/14
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedExceptionActivityItem extends ValueObjectSupport<ExecutedExceptionActivityItem> {
    private Date timestamp;
    private String userId;
    private String ip;
    private String refEntityName;
    private String refKey;
    private BigMoney amount;

    public ExecutedExceptionActivityItem(Date timestamp, String userId, String ip, String refEntityName, String refKey, BigMoney amount) {
        notNull(timestamp);
        this.timestamp = timestamp;
        this.ip = ip;
        this.userId = userId;
        this.refEntityName = refEntityName;
        this.refKey = refKey;
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getIp() {
        return ip;
    }

    public String getUserId() {
        return userId;
    }

    public String getRefEntityName() {
        return refEntityName;
    }

    public String getRefKey() {
        return refKey;
    }

    public BigMoney getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExecutedExceptionActivityItem that = (ExecutedExceptionActivityItem) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        if (refEntityName != null ? !refEntityName.equals(that.refEntityName) : that.refEntityName != null)
            return false;
        if (refKey != null ? !refKey.equals(that.refKey) : that.refKey != null) return false;
        if (!timestamp.equals(that.timestamp)) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (refEntityName != null ? refEntityName.hashCode() : 0);
        result = 31 * result + (refKey != null ? refKey.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ActivityItem{" +
                "timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", ip='" + ip + '\'' +
                ", refEntityName='" + refEntityName + '\'' +
                ", refKey='" + refKey + '\'' +
                ", amount=" + amount +
                '}';
    }

    public boolean timestampWithin(LocalDateTime from, LocalDateTime to) {
        notNull(from);
        notNull(to);
        notNull(from.isBefore(to), "from should be less than to");
        return timestampWithin(from.toDate(), to.toDate());
    }

    public boolean timestampWithin(Date from, Date to) {
        notNull(from);
        notNull(to);
        notNull(from.before(to), "from should be less than to");
        return from.getTime() <= timestamp.getTime() && to.getTime() > timestamp.getTime();
    }
}