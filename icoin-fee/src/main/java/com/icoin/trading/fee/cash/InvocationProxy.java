package com.icoin.trading.fee.cash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/26/14
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvocationProxy {
    private static Logger logger = LoggerFactory.getLogger(InvocationProxy.class);
    private final Invocation invocation;

    public InvocationProxy(Invocation invocation) {
        notNull(invocation);
        this.invocation = invocation;
    }

    public ValidationCode invoke() throws Exception {
        InvocationContext context = invocation.getInvocationContext();
        ValidationCode code;
        try {
            code = invocation.invoke();
        } finally {
//            System.err.println("Whole time :" + context.printProfiling());
            logger.info("Whole time : {} ", context.printProfiling());
        }

        return code;
    }

}