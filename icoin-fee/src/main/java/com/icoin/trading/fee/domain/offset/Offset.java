package com.icoin.trading.fee.domain.offset;

import com.homhon.base.domain.Identity;
import com.homhon.util.Collections;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.fee.domain.offset.CancelledReason;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.FeeItemType;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.offset.OffsetReason;
import com.icoin.trading.api.fee.domain.offset.OffsetStatus;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import com.icoin.trading.api.fee.events.offset.OffsetAmountNotMatchedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCancelledEvent;
import com.icoin.trading.api.fee.events.offset.OffsetStartedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.isTrue;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:21
 * To change this template use File | Settings | File Templates.
 */
public class Offset extends AxonAnnotatedAggregateRoot<Offset, String> {
    private static Logger logger = LoggerFactory.getLogger(Offset.class);

    @AggregateIdentifier
    @Identity
    private OffsetId offsetId;
    private BigMoney offsetAmount;

    private String accountId;
    private List<FeeItem> arapList;
    private List<FeeItem> receivedPaidList;
    private OffsetType offsetType;
    private OffsetStatus offsetStatus = OffsetStatus.NOT_OFFSETED;
    private OffsetReason offsetReason;
    private Date offsetDate;
    private Date cancelledDate;
    private CancelledReason cancelledReason;

    @SuppressWarnings("UnusedDeclaration")
    protected Offset() {
    }

    public Offset(OffsetId offsetId, OffsetType offsetType, String accountId, List<FeeItem> arapList, List<FeeItem> receivedPaidList, BigMoney offsetAmount, Date startedDate) {
        isTrue(isValid(arapList));
        isTrue(isValid(receivedPaidList));
        apply(new OffsetStartedEvent(offsetId, offsetType, accountId, arapList, receivedPaidList, offsetAmount, startedDate));
    }

    private boolean isValid(List<FeeItem> feeItems) {
        if (Collections.isEmpty(feeItems)) {
            return false;
        }

        FeeItemType type = null;

        for (FeeItem feeItem : feeItems) {
            if (type == null) {
                type = feeItem.getType();
                continue;
            }

            if (type != feeItem.getType()) {
                return false;
            }
        }
        return true;
    }


    public void offset(OffsetReason offsetReason, Date date) {
        changeStatus(offsetStatus.offset());

        BigMoney arapAmount = sumUp(arapList);
        BigMoney receivedPaidAmount = sumUp(receivedPaidList);

        if (receivedPaidAmount.isEqual(arapAmount) && offsetAmount.isEqual(receivedPaidAmount)) {
            apply(new OffsetedEvent(offsetId, offsetReason, offsetAmount, date));
        } else {
            apply(new OffsetAmountNotMatchedEvent(offsetId, offsetReason, offsetAmount, arapAmount, receivedPaidAmount, date));
        }
    }


    public void cancel(CancelledReason cancelledReason, Date date) {
        changeStatus(offsetStatus.cancel());
        apply(new OffsetCancelledEvent(offsetId, cancelledReason, date));
    }

    @EventHandler
    public void on(OffsetStartedEvent event) {
        this.offsetId = event.getOffsetId();
        this.accountId = event.getAccountId();
        this.arapList = event.getArapList();
        this.receivedPaidList = event.getReceivedPaidList();
        this.offsetAmount = event.getOffsetAmount();
        this.offsetType = event.getOffsetType();
    }

    @EventHandler
    public void on(OffsetedEvent event) {
        this.offsetDate = event.getOffsetDate();
        this.offsetReason = event.getOffsetReason();
    }

    @EventHandler
    public void on(OffsetCancelledEvent event) {
        this.cancelledDate = event.getDate();
        this.cancelledReason = event.getCancelledReason();

    }

    public OffsetId getOffsetId() {
        return offsetId;
    }

    public BigMoney getOffsetAmount() {
        return offsetAmount;
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

    public OffsetType getOffsetType() {
        return offsetType;
    }

    public OffsetStatus getOffsetStatus() {
        return offsetStatus;
    }

    public OffsetReason getOffsetReason() {
        return offsetReason;
    }

    public Date getOffsetDate() {
        return offsetDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public CancelledReason getCancelledReason() {
        return cancelledReason;
    }

    private BigMoney sumUp(List<FeeItem> arapList) {
        BigMoney money = null;
        for (FeeItem feeItem : arapList) {
            if (money == null) {
                money = feeItem.getAmount();
                continue;
            }

            money = money.plus(feeItem.getAmount());
        }

        return money;
    }

    public void setOffsetReason(OffsetReason offsetReason) {
        this.offsetReason = offsetReason;
    }

    private void setOffsetDate(Date offsetDate) {
        this.offsetDate = offsetDate;
    }

    private void changeStatus(OffsetStatus status) {
        if (offsetStatus != null && status != this.offsetStatus) {
            if (logger.isInfoEnabled()) {
                logger.debug("Offset#{}: changing status from {} to {}", identity(), this.offsetStatus, status);
            }
            this.offsetStatus = status;
        }
    }
}
