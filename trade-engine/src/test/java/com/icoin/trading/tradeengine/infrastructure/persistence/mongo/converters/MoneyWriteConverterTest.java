package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.mongodb.DBObject;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-12
 * Time: PM10:22
 * To change this template use File | Settings | File Templates.
 */
public class MoneyWriteConverterTest {

    @Test
    public void testConvert() throws Exception {
        final MoneyWriteConverter converter = new MoneyWriteConverter();

        final DBObject btc = converter.convert(BigMoney.of(CurrencyUnit.of(Currencies.BTC), 100));
        assertThat((Long) btc.get("amount"), equalTo(100 * 100000000L));
        assertThat((String) btc.get("currency"), equalTo(Currencies.BTC));

    }

    @Test
    public void testConvertCNY() throws Exception {
        final MoneyWriteConverter converter = new MoneyWriteConverter();

        final DBObject btc = converter.convert(BigMoney.of(CurrencyUnit.of(Currencies.CNY), 100));

        assertThat((Long) btc.get("amount"), equalTo(1000 * 100l));
        assertThat((String) btc.get("currency"), equalTo(Currencies.CNY));
    }
}
