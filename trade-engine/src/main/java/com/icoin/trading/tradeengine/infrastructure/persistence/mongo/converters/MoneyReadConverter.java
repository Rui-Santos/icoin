package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.homhon.base.domain.model.money.Money;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;


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

        Double amountSrc = ((Long) source.get("amount")).doubleValue()/ MONEY_PRECISION;
        String ccy = (String) source.get("ccy");
        if (amountSrc != null) {
            return Money.createMoney(amountSrc, Currency.getInstance(ccy));
        }

        return Money.ZERO;
    }
}
