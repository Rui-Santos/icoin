package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.homhon.base.domain.model.money.Money;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

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
        BasicDBObject result = new BasicDBObject();
        if (source.getAmount() != null)
            result.put("amount", (long)(source.getAmount().doubleValue()*MoneyReadConverter.MONEY_PRECISION));
        if (source.getCurrency() != null)
            result.put("ccy", source.getCurrencyCode());

        return result;
    }
}
