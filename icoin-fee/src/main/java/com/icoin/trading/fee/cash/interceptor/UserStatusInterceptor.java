package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.users.domain.model.user.UserAccount;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:01
 * To change this template use File | Settings | File Templates.
 */
public class UserStatusInterceptor extends ProfilingInterceptor {
    @Override
    protected ValidationCode doIntercept(Invocation invocation) {

        final InvocationContext context = invocation.getInvocationContext();
        UserAccount user = context.getUser();
        if (user == null) {
            return ValidationCode.USER_NOT_FOUND;
        }

        if (user.isLocked(context.getOccurringTime())) {
            return ValidationCode.USER_LOCKED;
        }
        return null;
    }
}