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

        assertThat(CurrencyPair.EUR_USD.getBaseCurrency(), equalTo("EUR"));
        assertThat(CurrencyPair.EUR_USD.getCounterCurrency(), equalTo("USD"));

        assertThat(CurrencyPair.GBP_USD.getBaseCurrency(), equalTo("GBP"));
        assertThat(CurrencyPair.GBP_USD.getCounterCurrency(), equalTo("USD"));

        assertThat(CurrencyPair.USD_JPY.getBaseCurrency(), equalTo("USD"));
        assertThat(CurrencyPair.USD_JPY.getCounterCurrency(), equalTo("JPY"));

        assertThat(CurrencyPair.USD_CHF.getBaseCurrency(), equalTo("USD"));
        assertThat(CurrencyPair.USD_CHF.getCounterCurrency(), equalTo("CHF"));

        assertThat(CurrencyPair.USD_AUD.getBaseCurrency(), equalTo("USD"));
        assertThat(CurrencyPair.USD_AUD.getCounterCurrency(), equalTo("AUD"));

        assertThat(CurrencyPair.USD_CAD.getBaseCurrency(), equalTo("USD"));
        assertThat(CurrencyPair.USD_CAD.getCounterCurrency(), equalTo("CAD"));
    }

    @Test
    public void testBitcoinCourtesy() {

        assertThat(CurrencyPair.BTC_USD.getBaseCurrency(), equalTo("BTC"));
        assertThat(CurrencyPair.BTC_USD.getCounterCurrency(), equalTo("USD"));

        assertThat(CurrencyPair.BTC_GBP.getBaseCurrency(), equalTo("BTC"));
        assertThat(CurrencyPair.BTC_USD.getCounterCurrency(), equalTo("USD"));

        assertThat(CurrencyPair.BTC_EUR.getBaseCurrency(), equalTo("BTC"));
        assertThat(CurrencyPair.BTC_EUR.getCounterCurrency(), equalTo("EUR"));

        assertThat(CurrencyPair.BTC_JPY.getBaseCurrency(), equalTo("BTC"));
        assertThat(CurrencyPair.BTC_JPY.getCounterCurrency(), equalTo("JPY"));

        assertThat(CurrencyPair.BTC_CHF.getBaseCurrency(), equalTo("BTC"));
        assertThat(CurrencyPair.BTC_CHF.getCounterCurrency(), equalTo("CHF"));

        assertThat(CurrencyPair.BTC_AUD.getBaseCurrency(), equalTo("BTC"));
        assertThat(CurrencyPair.BTC_AUD.getCounterCurrency(), equalTo("AUD"));

        assertThat(CurrencyPair.BTC_CAD.getBaseCurrency(), equalTo("BTC"));
        assertThat(CurrencyPair.BTC_CAD.getCounterCurrency(), equalTo("CAD"));

        assertThat(CurrencyPair.BTC_CNY.getBaseCurrency(), equalTo("BTC"));
        assertThat(CurrencyPair.BTC_CNY.getCounterCurrency(), equalTo("CNY"));

    }

}