package com.icoin.trading.fee.cash;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM8:59
 * To change this template use File | Settings | File Templates.
 */
public interface Invocation {
    ValidationCode invoke() throws Exception;

    InvocationContext getInvocationContext();

    ValidationCode getValidationCode();

    boolean isExecuted();
}
