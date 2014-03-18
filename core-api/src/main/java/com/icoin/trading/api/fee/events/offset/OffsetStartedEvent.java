package com.icoin.trading.api.fee.events.offset;

import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import org.joda.money.BigMoney;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:40
 * To change this template use File | Settings | File Templates.
 */
public class OffsetStartedEvent extends AbstractOffsetEvent<OffsetStartedEvent> {
    private final OffsetType offsetType;
    private final String accountId;
    private final List<FeeItem> arapList;
    private final List<FeeItem> receivedPaidList;
    private final BigMoney offsetAmount;
    private final Date startedDate;


    public OffsetStartedEvent(OffsetId offsetId, OffsetType offsetType, String accountId, List<FeeItem> arapList, List<FeeItem> receivedPaidList, BigMoney offsetAmount, Date startedDate) {
        super(offsetId);
        this.offsetType = offsetType;
        this.accountId = accountId;
        this.arapList = arapList;
        this.receivedPaidList = receivedPaidList;
        this.offsetAmount = offsetAmount;
        this.startedDate = startedDate;
    }

    public OffsetType getOffsetType() {
        return offsetType;
    }

    public String getAccountId() {
        return accountId;
    }

    public List<FeeItem> getArapList() {
        return arapList;
    }

    public List<FeeItem> getReceivedPaidList() {
        return receivedPaidList;
    }

    public BigMoney getOffsetAmount() {
        return offsetAmount;
    }

    public Date getStartedDate() {
        return startedDate;
    }
}
