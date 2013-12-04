package com.icoin.trading.tradeengine;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-4
 * Time: PM11:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class Constants {
    public static final int MONEY_SCALE = 3;
    public static final int COIN_SCALE = 8;
    public static BigDecimal LOWEST_PRICE = BigDecimal.valueOf(0.00000001);
    public static BigDecimal IGNORED_PRICE = BigDecimal.valueOf(0.000000005);
}
