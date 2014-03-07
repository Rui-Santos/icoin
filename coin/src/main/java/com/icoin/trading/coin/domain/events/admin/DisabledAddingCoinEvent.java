package com.icoin.trading.coin.domain.events.admin;


import com.homhon.base.domain.model.ValueObjectSupport;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM12:12
 * To change this template use File | Settings | File Templates.
 */

//RevivedAddingCoinEvent
public class DisabledAddingCoinEvent extends ValueObjectSupport<DisabledAddingCoinEvent> {
    private String lastChangedBy;
    private Date lastChangedTime;
    private ChangedReason reason;
}
