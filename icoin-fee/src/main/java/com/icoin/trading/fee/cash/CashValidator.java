package com.icoin.trading.fee.cash;

import com.icoin.trading.users.domain.model.user.UserAccount;
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
    ValidationCode canCreate(UserAccount user, BigMoney amount, Date occurringTime);
}
