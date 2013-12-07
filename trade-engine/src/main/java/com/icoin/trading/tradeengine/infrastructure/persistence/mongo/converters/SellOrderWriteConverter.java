package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-7
 * Time: AM2:50
 * To change this template use File | Settings | File Templates.
 */
public class SellOrderWriteConverter implements Converter<SellOrder,DBObject> {
    @Override
    public DBObject convert(SellOrder source) {
        if (source == null) {
            return null;
        }

        DBObject dbo = BasicDBObjectBuilder
                .start("_id", source.getPrimaryKey())
                .append("_class", source.getClass().getName())
                .append("itemPrice", source.getItemPrice().doubleValue())
                .append("tradeAmount", source.getTradeAmount().doubleValue())
                .append("itemRemaining", source.getItemRemaining().doubleValue())
                .append("placeDate", source.getPlaceDate())
                .append("orderType", source.getOrderType().toString())
                .append("orderBookId", source.getOrderBookId().toString())
                .append("orderStatus", source.getOrderStatus().toString())
                .append("version", source.getVersion())
                .get();
        return dbo;
    }
}