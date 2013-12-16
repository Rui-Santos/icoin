package com.icoin.trading.tradeengine;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.math.RoundingMode;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-15
 * Time: PM4:50
 * To change this template use File | Settings | File Templates.
 */
public abstract class MoneyUtils {

    public static BigMoney convertToBigMoney(String ccy, long amount) {
        return convertToBigMoney(ccy, amount, RoundingMode.HALF_EVEN);
    }

    public static BigMoney convertToBigMoney(String ccy, long amount, RoundingMode roundingMode) {
        hasLength(ccy);
        notNull(roundingMode);

        final CurrencyUnit currency = CurrencyUnit.of(ccy);
        final double multiplier = getMultiplier(currency);

        return BigMoney.of(currency, amount).dividedBy(multiplier, roundingMode);

    }

    public static double getMultiplier(CurrencyUnit currency) {
        final int decimalPlaces = currency.getDecimalPlaces();
        if (decimalPlaces < 0) {
            throw new UnsupportedOperationException("not support for ccy " + currency);
        }
        return Math.pow(10, decimalPlaces);
    }

    public static long convertToLong(BigMoney money) {
        notNull(money.getAmount());
        notNull(money.getCurrencyUnit());
        return convertToLong(money, RoundingMode.HALF_EVEN);
    }

    public static long convertToLong(BigMoney money, RoundingMode roundingMode) {
        return money.multiplyRetainScale(getMultiplier(money.getCurrencyUnit()), roundingMode).getAmountMajorLong();

    }
}
