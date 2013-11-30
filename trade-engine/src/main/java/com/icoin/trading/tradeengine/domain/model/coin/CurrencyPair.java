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
public class CurrencyPair extends ValueObjectSupport<CurrencyPair> {
    private String ccy1;
    private String ccy2;

    private CurrencyPair(String ccy1, String ccy2) {
        notNull(ccy1);
        notNull(ccy2);
        this.ccy1 = ccy1;
        this.ccy2 = ccy2;
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

        CurrencyPair that = (CurrencyPair) o;

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

    public static CurrencyPair createCurrencyPair(String ccy1, String ccy2) {
        return new CurrencyPair(ccy1, ccy2);
    }
}
