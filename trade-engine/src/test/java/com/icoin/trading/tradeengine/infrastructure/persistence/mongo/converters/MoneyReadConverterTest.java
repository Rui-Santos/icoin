package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
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
 * Time: PM10:57
 * To change this template use File | Settings | File Templates.
 */
public class MoneyReadConverterTest {
    @Test
    public void testConvert() throws Exception {
        final MoneyReadConverter converter = new MoneyReadConverter();

        final DBObject dbObject = BasicDBObjectBuilder.start("amount", 123456789L)
                .append("currency", Currencies.BTC)
                .get();

        final BigMoney money = converter.convert(dbObject);

        assertThat(money, notNullValue());
        assertThat(money.getAmount(), notNullValue());
        closeTo(money.getAmount(), BigDecimal.valueOf(1.23456789));
        assertThat(money.getCurrencyUnit(), equalTo(CurrencyUnit.of(Currencies.BTC)));
    }

    @Test
    public void testConvertCNY() throws Exception {
        final MoneyReadConverter converter = new MoneyReadConverter();

        final DBObject dbObject = BasicDBObjectBuilder.start("amount", 123456789L)
                .append("currency", Currencies.CNY)
                .get();

        final BigMoney money = converter.convert(dbObject);

        assertThat(money, notNullValue());
        assertThat(money.getAmount(), notNullValue());
        closeTo(money.getAmount(), BigDecimal.valueOf(1234567.89));
        assertThat(money.getCurrencyUnit(), equalTo(CurrencyUnit.of(Currencies.CNY)));
    }
}
