package com.icoin.trading.fee.cash;

import com.icoin.trading.fee.cash.interceptor.ProfilingInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:06
 * To change this template use File | Settings | File Templates.
 */
public class SleepInterceptor extends ProfilingInterceptor {
    private final int time;
    private boolean ran;

    public SleepInterceptor(int time) {
        this.time = time;
    }

    @Override
    protected ValidationCode doIntercept(Invocation invocation) throws Exception {
        TimeUnit.MILLISECONDS.sleep(time);
        ran = true;
        return null;
    }

    public boolean isRan() {
        return ran;
    }
}