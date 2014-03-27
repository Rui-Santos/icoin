package com.icoin.trading.fee.cash;

import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: PM9:33
 * To change this template use File | Settings | File Templates.
 */
public abstract class InterceptableCashValidator implements CashValidator{
    private static Logger logger = LoggerFactory.getLogger(InterceptableCashValidator.class);

    private List<Interceptor> interceptors;

    @Override
    public ValidationCode canCreate(String userId, BigMoney amount, Date occurringTime) throws Exception{

        InvocationProxy invocation = createInvocation(interceptors, userId, amount, occurringTime);
        ValidationCode resultCode = invocation.invoke();

        if (resultCode != ValidationCode.SUCCESSFUL) {
            logger.error("Creation error, userId: {}, amount: {}, date: {}, result code: {}", resultCode);
            return null;
        }

        return resultCode;
    }

    private InvocationProxy createInvocation(List<Interceptor> interceptors, String userId, BigMoney amount, Date occurringTime) {
        return null;
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }
}
