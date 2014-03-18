package com.icoin.trading.fee.domain.fee;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:31
 * To change this template use File | Settings | File Templates.
 */
public interface GeneralLedgerMarkable {
    /**
     * Mark if this record booked in GeneralLedger or not.
     */
    boolean isPosted();
}
