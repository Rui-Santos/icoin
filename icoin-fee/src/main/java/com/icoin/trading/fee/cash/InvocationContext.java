package com.icoin.trading.fee.cash;

import org.joda.money.BigMoney;
import org.springframework.util.StopWatch;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;
/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:00
 * To change this template use File | Settings | File Templates.
 */
public class InvocationContext {
    private final String userId;
    private final BigMoney amount;
    private final Date occurringTime;
    private final StopWatch stopWatch = new StopWatch("invoke: ");

    public InvocationContext(String userId, BigMoney amount, Date occurringTime) {
        notNull(userId);
        notNull(amount);
        notNull(occurringTime);

        this.userId = userId;
        this.amount = amount;
        this.occurringTime = occurringTime;
    }

    public String getUserId() {
        return userId;
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

    public void stopProfiling(){
        stopWatch.stop();
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public String printProfiling(){
        return stopWatch.prettyPrint();
    }

    @Override
    public String toString() {
        return "InvocationContext{" +
                "userId='" + userId + '\'' +
                ", amount=" + amount +
                ", occurringTime=" + occurringTime +
                '}';
    }
}