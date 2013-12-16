package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.icoin.trading.tradeengine.MoneyUtils;
import com.mongodb.DBObject;
import org.joda.money.BigMoney;
import org.springframework.core.convert.converter.Converter;


/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-4-30
 * Time: AM8:57
 * To change this homhon use File | Settings | File Templates.
 */
public class MoneyReadConverter implements Converter<DBObject, BigMoney> {

    @Override
    public BigMoney convert(DBObject source) {
        if (source == null) {
            return null;
        }

        final String ccy = (String) source.get("currency");
        final Object amount = source.get("amount");

        if (amount == null) {
            throw new UnsupportedOperationException("not support for nullable amount of " + source);
        }

        return MoneyUtils.convertToBigMoney(ccy, (Long) amount);
    }
}
