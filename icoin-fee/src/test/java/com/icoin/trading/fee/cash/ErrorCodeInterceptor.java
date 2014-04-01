package com.icoin.trading.fee.cash;

import com.icoin.trading.fee.cash.interceptor.ProfilingInterceptor;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: PM9:50
 * To change this template use File | Settings | File Templates.
 */
public class ErrorCodeInterceptor extends ProfilingInterceptor {
    @Override
    protected ValidationCode doIntercept(Invocation invocation) {
        return ValidationCode.SYSTEM_DISALLOWED;
    }
}