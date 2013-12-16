package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-12
 * Time: PM10:23
 * To change this template use File | Settings | File Templates.
 */
public class MoneyTest {
    //class file:/org/joda/money/MoneyDataExtension.csv
    @Test
    public void testJodaMoneyWithExtension() throws Exception {
        // create a monetary value
        Money usdMoney = Money.parse("USD 23.87");
        Money cnyMoney = Money.parse("CNY 86.35");
        Money btcMoney = Money.parse("BTC 23.87234232");

        assertThat(usdMoney, notNullValue());
        assertThat(usdMoney.getCurrencyUnit(), notNullValue());
        assertThat(usdMoney.getCurrencyUnit(), equalTo(CurrencyUnit.of(Currencies.USD)));
        closeTo(usdMoney.getAmount(), BigDecimal.valueOf(23.87));

        assertThat(cnyMoney, notNullValue());
        assertThat(cnyMoney.getCurrencyUnit(), notNullValue());
        assertThat(cnyMoney.getCurrencyUnit(), equalTo(CurrencyUnit.of(Currencies.CNY)));
        closeTo(cnyMoney.getAmount(), BigDecimal.valueOf(86.35));

        assertThat(btcMoney, notNullValue());
        assertThat(btcMoney.getCurrencyUnit(), notNullValue());
        assertThat(btcMoney.getCurrencyUnit(), equalTo(CurrencyUnit.of(Currencies.BTC)));
        closeTo(btcMoney.getAmount(), BigDecimal.valueOf(23.87234232));

    }
}
