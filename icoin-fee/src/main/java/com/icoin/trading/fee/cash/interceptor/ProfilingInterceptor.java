package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Interceptor;
import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ResultCode;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM8:54
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProfilingInterceptor implements Interceptor {

    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public ResultCode intercept(Invocation invocation) throws Exception {
        InvocationContext context = invocation.getInvocationContext();
        context.startProfiling("interceptor: " + getName());
        ResultCode code;
        try {
            code = doIntercept(invocation);
            if (ResultCode.breakDown(code)) {
                return code;
            }
        } finally {
            context.stopProfiling();
        }
        return invocation.invoke();
    }

    protected abstract ResultCode doIntercept(Invocation invocation) throws Exception ;
}