package com.icoin.trading.api.fee.command.offset;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/18/14
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateOffsetCommand extends CommandSupport<CreateOffsetCommand> {
    @NotNull
    private final OffsetId offsetId;
    @NotNull
    private final OffsetType offsetType;
    @NotEmpty
    private final String accountId;
    @NotEmpty
    private final List<FeeItem> arapList;
    @NotEmpty
    private final List<FeeItem> receivedPaidList;
    @NotNull
    private final BigMoney offsetAmount;
    @NotNull
    private final Date startedDate;

    public CreateOffsetCommand(OffsetId offsetId, OffsetType offsetType, String accountId, List<FeeItem> arapList, List<FeeItem> receivedPaidList, BigMoney offsetAmount, Date startedDate) {
        this.offsetId = offsetId;
        this.offsetType = offsetType;
        this.accountId = accountId;
        this.arapList = arapList;
        this.receivedPaidList = receivedPaidList;
        this.offsetAmount = offsetAmount;
        this.startedDate = startedDate;
    }

    public OffsetId getOffsetId() {
        return offsetId;
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