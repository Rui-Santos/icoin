package com.icoin.trading.tradeengine.application;

import org.axonframework.common.lock.IdentifierBasedLock;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-23
 * Time: PM11:39
 * To change this template use File | Settings | File Templates.
 */
public class SynchronizedOnIdentifierHandler {
    private final IdentifierBasedLock lock = new IdentifierBasedLock();
    private boolean obtainLock;

    public <V> V perform(Callback<V> callback) {
        notNull(callback);
        notNull(callback.getIdentifier());

        if (obtainLock) {
            lock.obtainLock(callback.getIdentifier());
        }
        try {
            return callback.execute();
        } catch (Exception e) {
            throw new InvocationException("Invoke on " + callback.getIdentifier() + " error", e);
        } finally {
            if (obtainLock) {
                lock.releaseLock(callback.getIdentifier());
            }
        }
    }
}
