package com.icoin.trading.fee.cash;

import com.icoin.trading.fee.domain.cash.Cash;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/26/14
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CashFactory<T extends Cash> {
    T createCash(String userId, BigMoney amount, Date occurringTime);
}