package com.icoin.trading.tradeengine.application;

import com.homhon.core.exception.IZookeyException;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-23
 * Time: PM11:56
 * To change this template use File | Settings | File Templates.
 */
public class InvocationException extends IZookeyException {
    private Exception target;

    public InvocationException(String string, Exception root) {
        super(string, root);
        this.target = root;
    }

    /**
     * Get the thrown target exception.
     *
     * <p>This method predates the general-purpose exception chaining facility.
     * The {@link Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @return the thrown target exception (cause of this exception).
     */
    public Exception getTargetException() {
        return target;
    }

    /**
     * Returns the cause of this exception (the thrown target exception,
     * which may be {@code null}).
     *
     * @return  the cause of this exception.
     * @since   1.4
     */
    public Exception getCause() {
        return target;
    }
}
