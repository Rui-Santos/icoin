package com.icoin.trading.fee.cash.factory;

import com.icoin.trading.fee.domain.cash.CoinWithdrawCash;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:14
 * To change this template use File | Settings | File Templates.
 */
public class CoinWithdrawFactory extends AbstractCashFactory<CoinWithdrawCash> {

    @Override
    protected CoinWithdrawCash doCreate(String userId, BigMoney amount, Date occurringTime) {
        CoinWithdrawCash cash = new CoinWithdrawCash();
        return cash;
    }


}
