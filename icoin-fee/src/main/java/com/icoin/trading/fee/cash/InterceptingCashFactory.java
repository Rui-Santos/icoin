package com.icoin.trading.fee.cash;

import com.icoin.trading.fee.domain.coin.CoinCash;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/26/14
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class InterceptingCashFactory<T extends CoinCash> implements CashFactory<T> {
    private List<Interceptor> interceptors;
    private static Logger logger = LoggerFactory.getLogger(InterceptingCashFactory.class);

    @Override
    public T createCash(String userId, BigMoney amount, Date occurringTime) {

        try {
            InvocationProxy invocation = createInvocation(interceptors, userId, amount, occurringTime);
            ResultCode resultCode = invocation.invoke();

            if (resultCode != ResultCode.COMPLETE) {
                logger.error("Creation error, userId: {}, amount: {}, date: {}, result code: {}", resultCode);
                return null;
            }

            return create(userId, amount, occurringTime);
        } catch (Exception e) {
            logger.error("Creation error, userId: {}, amount: {}, date: {}, error msg: {}", e);
        }
        return null;
    }

    protected abstract T create(String userId, BigMoney amount, Date occurringTime);

    private InvocationProxy createInvocation(List<Interceptor> interceptors, String userId, BigMoney amount, Date occurringTime) {
        return new InvocationProxy(new DefaultInvocation(new InvocationContext(userId, amount, occurringTime), interceptors));
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }
}