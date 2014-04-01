package com.icoin.trading.fee.cash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.homhon.util.Collections.isEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/26/14
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultInvocation implements Invocation {
    private static Logger logger = LoggerFactory.getLogger(DefaultInvocation.class);
    protected ValidationCode validationCode;
    protected Iterator<? extends Interceptor> interceptors = Collections.EMPTY_LIST.iterator();
    private boolean executed;
    protected InvocationContext invocationContext;

    public DefaultInvocation(InvocationContext invocationContext, List<? extends Interceptor> interceptorList) {
        if (!isEmpty(interceptorList)) {
            interceptors = interceptorList.iterator();
        }
        this.invocationContext = invocationContext;
    }

    @Override
    public ValidationCode invoke(){
        try {
            if (interceptors.hasNext() && !executed) {
                Interceptor interceptor = interceptors.next();
                ValidationCode code = interceptor.intercept(this);

                if (ValidationCode.breakDown(code)) {
                    executed = true;
                    validationCode = code;
                }
            } else {
                executed = true;
                validationCode = ValidationCode.SUCCESSFUL;
            }
        } catch (Exception e) {
            executed = true;
            validationCode = ValidationCode.EXECUTION_ERROR;
            logger.error("Invocation error with context {}", invocationContext, e);
        }
        return validationCode;
    }


    @Override
    public ValidationCode getValidationCode() {
        return validationCode;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public InvocationContext getInvocationContext() {
        return invocationContext;
    }
}