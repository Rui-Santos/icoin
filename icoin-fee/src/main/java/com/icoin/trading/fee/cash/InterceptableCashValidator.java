package com.icoin.trading.fee.cash;

import com.icoin.trading.users.domain.model.user.UserAccount;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasText;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: PM9:33
 * To change this template use File | Settings | File Templates.
 */
public abstract class InterceptableCashValidator implements CashValidator {
    private static Logger logger = LoggerFactory.getLogger(InterceptableCashValidator.class);

    private List<Interceptor> interceptors;

    @Override
    public ValidationCode canCreate(UserAccount user, String portfolioId, BigMoney amount, Date occurringTime) {
        notNull(user);
        hasText(portfolioId);
        InvocationProxy invocation = createInvocation(interceptors, user, portfolioId, amount, occurringTime);
        ValidationCode resultCode = invocation.invoke();

        if (resultCode != ValidationCode.SUCCESSFUL) {
            logger.error("Creation error, userId: {}, amount: {}, date: {}, result code: {}", resultCode);
            return null;
        }

        return resultCode;
    }

    private InvocationProxy createInvocation(List<Interceptor> interceptors, UserAccount user, String portfolioId, BigMoney amount, Date occurringTime) {
        final InvocationProxy proxy = new InvocationProxy(new DefaultInvocation(
                new InvocationContext(user, portfolioId, amount, occurringTime), interceptors));

        return proxy;
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }
}
