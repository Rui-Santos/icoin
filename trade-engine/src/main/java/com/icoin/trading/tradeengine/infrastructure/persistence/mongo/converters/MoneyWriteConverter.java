package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-4-30
 * Time: AM8:57
 * To change this homhon use File | Settings | File Templates.
 */
public class MoneyWriteConverter implements Converter<Money, DBObject> {

    @Override
    public DBObject convert(Money source) {
        if (source == null) {
            return null;
        }

        notNull(source.getAmount());
        notNull(source.getCurrencyUnit());

        final CurrencyUnit currency = source.getCurrencyUnit();
        final int decimalPlaces = currency.getDecimalPlaces();

        if(decimalPlaces<0){
           throw new UnsupportedOperationException("not support for ccy " + currency);
        }

        final BigDecimal unit = BigDecimal.TEN.pow(decimalPlaces);

        BasicDBObject result = new BasicDBObject();
        result.put("amount", source.getAmount().multiply(unit).longValue());
        result.put("ccy", currency.getCurrencyCode());
        return result;
    }
}
