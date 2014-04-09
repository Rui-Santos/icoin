package com.icoin.trading.fee.cash;

import com.icoin.trading.users.domain.model.user.UserAccount;
import org.joda.money.BigMoney;
import org.springframework.util.StopWatch;

import java.util.Date;

import static com.homhon.util.Asserts.hasText;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:00
 * To change this template use File | Settings | File Templates.
 */
public class InvocationContext {
    private final UserAccount user;
    private final String portfolioId;
    private final BigMoney amount;
    private final Date occurringTime;
    private final StopWatch stopWatch = new StopWatch("invoke: ");

    public InvocationContext(UserAccount user, String portfolioId, BigMoney amount, Date occurringTime) {
        notNull(user);
        hasText(portfolioId);
        notNull(amount);
        isTrue(amount.isPositive(), "Amount should be greater than zero!");
        notNull(occurringTime);

        this.user = user;
        this.portfolioId = portfolioId;
        this.amount = amount;
        this.occurringTime = occurringTime;
    }

    public String getUserId() {
        return user.getPrimaryKey();
    }

    public UserAccount getUser() {
        return user;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public Date getOccurringTime() {
        return occurringTime;
    }

    public void startProfiling(String invocationName) {
        stopWatch.start(invocationName);
    }

    public void stopProfiling() {
        stopWatch.stop();
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public String printProfiling() {
        return stopWatch.prettyPrint();
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    @Override
    public String toString() {
        return "InvocationContext{" +
                "user=" + user +
                "portfolioId=" + portfolioId +
                ", amount=" + amount +
                ", occurringTime=" + occurringTime +
                '}';
    }
}