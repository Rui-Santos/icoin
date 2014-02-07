package com.icoin.trading.tradeengine.query.tradeexecuted;


import com.icoin.trading.tradeengine.MoneyUtils;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-5
 * Time: AM10:55
 * To change this template use File | Settings | File Templates.
 */
public class OpenHighLowCloseVolumeBuilder {
    private CurrencyUnit priceCurrency;
    private CurrencyUnit amountCurrency;

    private Date date;
    private Long open;
    private Long high;
    private Long low;
    private Long close;
    private Long volume;

    public OpenHighLowCloseVolumeBuilder(String priceCurrency, String amountCurrency) {
        notNull(priceCurrency);
        notNull(amountCurrency);
        this.priceCurrency = CurrencyUnit.of(priceCurrency);
        this.amountCurrency = CurrencyUnit.of(amountCurrency);
    }

    public void withDate(Date date) {
        this.date = date;
    }

    public void withOpen(Long open) {
        this.open = open;
    }

    public void withHigh(Long high) {
        this.high = high;
    }

    public void withLow(Long low) {
        this.low = low;
    }

    public void withClose(Long close) {
        this.close = close;
    }

    public void withVolume(Long volume) {
        this.volume = volume;
    }

    public OpenHighLowCloseVolume build() {
//        BigMoney o = MoneyUtils.convertToBigMoney(priceCurrency, open);
//        BigMoney h = MoneyUtils.convertToBigMoney(priceCurrency, open);
//        BigMoney l = MoneyUtils.convertToBigMoney(priceCurrency, open);
//        BigMoney c = MoneyUtils.convertToBigMoney(priceCurrency, open);
//        BigMoney v = MoneyUtils.convertToBigMoney(priceCurrency, open);
//
//
//        return new OpenHighLowCloseVolume(priceCurrency, amountCurrency, date, o, h, l, c, v);

        return null;
    }
}
