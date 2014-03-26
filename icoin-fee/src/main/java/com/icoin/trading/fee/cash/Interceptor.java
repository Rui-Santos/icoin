package com.icoin.trading.fee.cash;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM8:55
 * To change this template use File | Settings | File Templates.
 */
public interface Interceptor {
    ResultCode intercept(Invocation invocation) throws Exception;
}
