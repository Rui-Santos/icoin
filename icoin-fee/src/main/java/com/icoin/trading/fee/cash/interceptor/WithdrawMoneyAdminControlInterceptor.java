package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.cash.CashAdmin;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-29
 * Time: AM9:18
 * To change this template use File | Settings | File Templates.
 */
public class WithdrawMoneyAdminControlInterceptor extends AbstractAdminControlInterceptor {

    @Override
    protected ValidationCode doIntercept(Invocation invocation) {
        final CashAdmin admin = retrieve();
        if (admin == null) {
            return null;
        }

        return admin.canWithdrawMoney() ? null : ValidationCode.SYSTEM_DISALLOWED;
    }
}
