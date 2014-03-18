package com.icoin.trading.fee.domain.fee;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelReason;
import com.icoin.trading.api.fee.domain.fee.FeeMovingDirection;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import org.joda.money.BigMoney;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFee<T extends AbstractFee> extends ValueObjectSupport<T> implements GeneralLedgerMarkable {
    private static final long serialVersionUID = 6958573066090722942L;

    private FeeStatus feeStatus;
    private BigMoney amount;
    private FeeType feeType;
    private Date dueDate;
    private Date confirmedDate;
    private Date cancelledDate;
    private CancelReason cancelReason;
    private Date businessCreationTime;
    private boolean offseted;
    private Date offsetDate;
    private Date postDate;
    private boolean posted;
    private String userAccountId;
    private String offsetId;
    private BusinessType businessType;
    //like order id, like interest rates from back
    private String businessReferenceId;

    public CancelReason getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(CancelReason cancelReason) {
        this.cancelReason = cancelReason;
    }

    public AbstractFee(FeeType feeType) {
        notNull(feeType);
        this.feeType = feeType;
    }

    @Override
    public boolean isPosted() {
        return posted;
    }

    public FeeMovingDirection getFeeMovingDirection() {
        return feeType.getMovingDirection();
    }

    public FeeStatus getFeeStatus() {
        return feeStatus;
    }

    public void setFeeStatus(FeeStatus feeStatus) {
        this.feeStatus = feeStatus;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public void setAmount(BigMoney amount) {
        this.amount = amount;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    @SuppressWarnings("unused")
    private void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getBusinessCreationTime() {
        return businessCreationTime;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isOffseted() {
        return offseted;
    }

    public void setOffseted(boolean offseted) {
        this.offseted = offseted;
    }

    public Date getOffsetDate() {
        return offsetDate;
    }

    public void setOffsetDate(Date offsetDate) {
        this.offsetDate = offsetDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    public void setBusinessCreationTime(Date businessCreationTime) {
        this.businessCreationTime = businessCreationTime;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public Date getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(Date confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getOffsetId() {
        return offsetId;
    }

    public void setOffsetId(String offsetId) {
        this.offsetId = offsetId;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public String getBusinessReferenceId() {
        return businessReferenceId;
    }

    public void setBusinessReferenceId(String businessReferenceId) {
        this.businessReferenceId = businessReferenceId;
    }

    static AbstractFee initializeFee(AbstractFee<?> fee,
                                     BigMoney amount,
                                     String accountId,
                                     Date businessCreationTime,
                                     Date dueDate) {
        notNull(fee);

        Date now = currentTime();
        fee.setAmount(amount);
        fee.setUserAccountId(accountId);
        fee.setFeeStatus(FeeStatus.PENDING);
        fee.setBusinessCreationTime(businessCreationTime == null ? now : businessCreationTime);
        fee.setDueDate(dueDate == null ? now : dueDate);
        return fee;
    }
}