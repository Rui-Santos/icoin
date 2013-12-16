package com.icoin.trading.tradeengine;

import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import org.joda.money.CurrencyUnit;


/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-4
 * Time: PM11:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class Constants {
    public static CurrencyUnit DEFAULT_CURRENCY_UNIT = CurrencyUnit.of(Currencies.CNY);
    public static CurrencyUnit CURRENCY_UNIT_BTC = CurrencyUnit.of(Currencies.BTC);
    public static CurrencyUnit CURRENCY_UNIT_LTC = CurrencyUnit.of(Currencies.LTC);
    public static CurrencyUnit CURRENCY_UNIT_PPC = CurrencyUnit.of(Currencies.PPC);
    public static CurrencyUnit CURRENCY_UNIT_XPM = CurrencyUnit.of(Currencies.XPM);
}
