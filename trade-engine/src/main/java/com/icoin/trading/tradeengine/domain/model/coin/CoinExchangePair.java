package com.icoin.trading.tradeengine.domain.model.coin;


import com.homhon.base.domain.model.ValueObjectSupport;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-23
 * Time: PM10:57
 * To change this template use File | Settings | File Templates.
 */
public class CoinExchangePair extends ValueObjectSupport<CoinExchangePair> {
    public static final String CCY_DEFAULT = "CNY";
    public static final String CCY_USD = "USD";
    private String ccy1;
    private String ccy2;

    private CoinExchangePair(String ccy1, String ccy2) {
        notNull(ccy1);
        notNull(ccy2);

        //put the smaller one first
        if (ccy1.compareTo(ccy2) > 0) {
            this.ccy2 = ccy1;
            this.ccy1 = ccy2;
        }else{
            this.ccy1 = ccy1;
            this.ccy2 = ccy2;
        }
    }

    public String getCcy1() {
        return ccy1;
    }

    public String getCcy2() {
        return ccy2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoinExchangePair that = (CoinExchangePair) o;

        if (!ccy1.equals(that.ccy1)) return false;
        if (!ccy2.equals(that.ccy2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ccy1.hashCode();
        result = 31 * result + ccy2.hashCode();
        return result;
    }

    public static CoinExchangePair createCoinExchangePair(String ccy1, String ccy2) {
        return new CoinExchangePair(ccy1, ccy2);
    }

    public static CoinExchangePair createExchangeToDefault(String coinId) {
        return new CoinExchangePair(coinId, CCY_DEFAULT);
    }

    public static CoinExchangePair createExchangeToUSD(String ccy1) {
        return new CoinExchangePair(ccy1, CCY_USD);
    }
}
