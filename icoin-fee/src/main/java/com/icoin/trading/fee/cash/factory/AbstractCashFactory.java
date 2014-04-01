package com.icoin.trading.fee.cash.factory;

import com.icoin.trading.fee.cash.CashFactory;
import com.icoin.trading.fee.domain.DueDateService;
import com.icoin.trading.fee.domain.cash.Cash;
import org.joda.money.BigMoney;

import java.util.Date;
import java.util.Random;

import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.TimeUtils.currentTime;
import static com.homhon.util.TimeUtils.futureMillis;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/27/14
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCashFactory<T extends Cash> implements CashFactory<T> {
    private final Random random = new Random();
    private BigMoney approvedThresholdAmount;
    private int spanMillis = 2000;
    private DueDateService dueDateService;

    public T createCash(String userId, BigMoney amount, Date occurringTime) {
        T cash = doCreate(userId, amount, occurringTime);
        notNull(cash);
        computeApproved(cash);
        computeDueDate(occurringTime, cash);
        computeExecutionTime(cash, occurringTime);
        return cash;
    }

    private void computeExecutionTime(T cash, Date occurringTime) {
        cash.setScheduledTime(futureMillis(occurringTime, spanMillis));
    }

    private void computeDueDate(Date occurringTime, T cash) {
        final Date dueDate = dueDateService.computeDueDate(occurringTime);
        cash.setDueDate(dueDate);
    }


    private Date computeTime(Date occurringTime) {
        Date now = currentTime();
        //max time
        Date base = occurringTime.before(now) ? now : occurringTime;

        int nextMillis = spanMillis + random.nextInt(spanMillis / 2);
        return futureMillis(base, nextMillis);
    }

    public void setSpanMillis(int spanMillis) {
        this.spanMillis = spanMillis;
    }


    private void computeApproved(T cash) {
        boolean approved = true;
        if (approvedThresholdAmount.compareTo(cash.getAmount()) <= 0) {
            approved = false;
        }
        cash.setApproved(approved);
    }

    protected abstract T doCreate(String userId, BigMoney amount, Date occurringTime);

    public void setApprovedThresholdAmount(BigMoney approvedThresholdAmount) {
        this.approvedThresholdAmount = approvedThresholdAmount;
    }

    public void setDueDateService(DueDateService dueDateService) {
        this.dueDateService = dueDateService;
    }
}
