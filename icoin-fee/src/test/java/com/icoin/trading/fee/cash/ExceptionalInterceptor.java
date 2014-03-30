package com.icoin.trading.fee.cash;

import com.icoin.trading.fee.cash.interceptor.ProfilingInterceptor;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/26/14
 * Time: 6:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionalInterceptor extends ProfilingInterceptor {
    @Override
    protected ValidationCode doIntercept(Invocation invocation) throws Exception {
        throw new RuntimeException("Test blocking the invocation");
    }
}