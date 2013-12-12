package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.mongodb.DBObject;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-4-30
 * Time: AM8:57
 * To change this homhon use File | Settings | File Templates.
 */
public class MoneyReadConverter implements Converter<DBObject, Money> {

    public static final int MONEY_PRECISION = 100;

    @Override
    public Money convert(DBObject source) {
        if (source == null) {
            return null;
        }

        String ccy = (String) source.get("ccy");
        final CurrencyUnit currency = CurrencyUnit.of(ccy);

        final int decimalPlaces = currency.getDecimalPlaces();
        if (decimalPlaces < 0) {
            throw new UnsupportedOperationException("not support for ccy " + currency);
        }

        final Object amount = source.get("amount");

        if (amount == null) {
            throw new UnsupportedOperationException("not support for nullable amount of " + source);
        }

        if (!Long.class.isAssignableFrom(amount.getClass())) {
            throw new UnsupportedOperationException("not support for amount " + amount + ", class " + amount.getClass());
        }

        final BigDecimal unit = BigDecimal.TEN.pow(decimalPlaces);
        final BigDecimal value = BigDecimal.valueOf((Long) amount)
                .divide(unit, decimalPlaces, RoundingMode.HALF_EVEN);

        return Money.of(currency, value);
    }
}
