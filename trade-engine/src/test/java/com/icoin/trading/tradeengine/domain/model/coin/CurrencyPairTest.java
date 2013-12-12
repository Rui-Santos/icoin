package com.icoin.trading.tradeengine.domain.model.coin;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-12
 * Time: AM9:14
 * To change this template use File | Settings | File Templates.
 */
public class CurrencyPairTest {

    @Test
    public void testMajors() {

        assertThat(CurrencyPair.EUR_USD.baseCurrency, equalTo("EUR"));
        assertThat(CurrencyPair.EUR_USD.counterCurrency, equalTo("USD"));

        assertThat(CurrencyPair.GBP_USD.baseCurrency, equalTo("GBP"));
        assertThat(CurrencyPair.GBP_USD.counterCurrency, equalTo("USD"));

        assertThat(CurrencyPair.USD_JPY.baseCurrency, equalTo("USD"));
        assertThat(CurrencyPair.USD_JPY.counterCurrency, equalTo("JPY"));

        assertThat(CurrencyPair.USD_CHF.baseCurrency, equalTo("USD"));
        assertThat(CurrencyPair.USD_CHF.counterCurrency, equalTo("CHF"));

        assertThat(CurrencyPair.USD_AUD.baseCurrency, equalTo("USD"));
        assertThat(CurrencyPair.USD_AUD.counterCurrency, equalTo("AUD"));

        assertThat(CurrencyPair.USD_CAD.baseCurrency, equalTo("USD"));
        assertThat(CurrencyPair.USD_CAD.counterCurrency, equalTo("CAD"));
    }

    @Test
    public void testBitcoinCourtesy() {

        assertThat(CurrencyPair.BTC_USD.baseCurrency, equalTo("BTC"));
        assertThat(CurrencyPair.BTC_USD.counterCurrency, equalTo("USD"));

        assertThat(CurrencyPair.BTC_GBP.baseCurrency, equalTo("BTC"));
        assertThat(CurrencyPair.BTC_USD.counterCurrency, equalTo("USD"));

        assertThat(CurrencyPair.BTC_EUR.baseCurrency, equalTo("BTC"));
        assertThat(CurrencyPair.BTC_EUR.counterCurrency, equalTo("EUR"));

        assertThat(CurrencyPair.BTC_JPY.baseCurrency, equalTo("BTC"));
        assertThat(CurrencyPair.BTC_JPY.counterCurrency, equalTo("JPY"));

        assertThat(CurrencyPair.BTC_CHF.baseCurrency, equalTo("BTC"));
        assertThat(CurrencyPair.BTC_CHF.counterCurrency, equalTo("CHF"));

        assertThat(CurrencyPair.BTC_AUD.baseCurrency, equalTo("BTC"));
        assertThat(CurrencyPair.BTC_AUD.counterCurrency, equalTo("AUD"));

        assertThat(CurrencyPair.BTC_CAD.baseCurrency, equalTo("BTC"));
        assertThat(CurrencyPair.BTC_CAD.counterCurrency, equalTo("CAD"));

        assertThat(CurrencyPair.BTC_CNY.baseCurrency, equalTo("BTC"));
        assertThat(CurrencyPair.BTC_CNY.counterCurrency, equalTo("CNY"));

    }

}