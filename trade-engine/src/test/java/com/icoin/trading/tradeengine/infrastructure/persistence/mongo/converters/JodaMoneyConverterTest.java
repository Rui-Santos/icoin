package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.thoughtworks.xstream.XStream;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-15
 * Time: AM11:40
 * To change this template use File | Settings | File Templates.
 */
public class JodaMoneyConverterTest {
    private final XStream xs = new XStream();
    @Before
    public void setUp() throws Exception {
        xs.registerConverter(new JodaMoneyConverter());
    }

    @Test
    public void testBigMoney() throws Exception {
        final BigMoney money = BigMoney.of(CurrencyUnit.EUR, 20.098);

        final String moneyXml = xs.toXML(money);
        final BigMoney mashallingBack = (BigMoney) xs.fromXML(moneyXml);
        assertThat(mashallingBack, equalTo(money));
    }

    @Test
    public void testMoney() throws Exception {
        final Money money = Money.of(CurrencyUnit.of(Currencies.CNY), 20.09);

        final String moneyXml = xs.toXML(money);
        final Money mashallingBack = (Money) xs.fromXML(moneyXml);
        assertThat(mashallingBack, equalTo(money));
    }

    @Test
    public void testCurrencyUnit() throws Exception {
        final CurrencyUnit currencyUnit = CurrencyUnit.of(Currencies.CNY);

        final String currencyUnitXml = xs.toXML(currencyUnit);
        final CurrencyUnit mashallingBack = (CurrencyUnit) xs.fromXML(currencyUnitXml);
        assertThat(mashallingBack, equalTo(currencyUnit));
    }
}
