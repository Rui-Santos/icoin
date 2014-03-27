package com.icoin.trading.fee.cash;

import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: PM9:18
 * To change this template use File | Settings | File Templates.
 */
public interface CashValidator {
    ValidationCode canCreate(String userId, BigMoney amount, Date occurringTime) throws Exception;
}
