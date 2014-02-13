package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.icoin.trading.tradeengine.MoneyUtils;
import com.mongodb.BasicDBObject;
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
public class MoneyWriteConverter implements Converter<BigMoney, DBObject> {

    @Override
    public DBObject convert(BigMoney source) {
        if (source == null) {
            return null;
        }

        BasicDBObject result = new BasicDBObject();

        final long moneyLong = MoneyUtils.convertToLong(source);

        result.put("amount", moneyLong);
        result.put("currency", source.getCurrencyUnit().getCurrencyCode());
        return result;
    }
}
