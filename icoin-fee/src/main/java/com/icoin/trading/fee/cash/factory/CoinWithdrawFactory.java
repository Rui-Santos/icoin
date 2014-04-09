package com.icoin.trading.fee.cash.factory;

import com.icoin.trading.fee.domain.cash.CoinPayCash;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:14
 * To change this template use File | Settings | File Templates.
 */
public class CoinWithdrawFactory extends AbstractCashFactory<CoinPayCash> {

    @Override
    protected CoinPayCash doCreate(String userId, BigMoney amount, Date occurringTime) {
        CoinPayCash cash = new CoinPayCash();
        return cash;
    }


}
