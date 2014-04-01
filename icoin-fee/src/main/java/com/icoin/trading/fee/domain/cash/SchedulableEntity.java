package com.icoin.trading.fee.domain.cash;

import com.homhon.base.domain.Entity;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:25
 * To change this template use File | Settings | File Templates.
 */
public interface SchedulableEntity<S, ID> extends Entity<S, ID> {
    Date getScheduledTime();
}
