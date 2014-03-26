package com.icoin.trading.fee.cash;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM8:59
 * To change this template use File | Settings | File Templates.
 */
public interface Invocation {
    ResultCode invoke() throws Exception;

    InvocationContext getInvocationContext();

    ResultCode getResultCode();

    boolean isExecuted();
}
