package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-7
 * Time: AM2:50
 * To change this template use File | Settings | File Templates.
 */
public class SellOrderReadConverter implements Converter<DBObject,SellOrder> {
    private static Logger logger = LoggerFactory.getLogger(SellOrderReadConverter.class);

    @Override
    public SellOrder convert(DBObject source) {
        if (source == null) {
            return null;
        }

        final OrderType orderType = OrderType.valueOf((String)source.get("orderType"));

        if(orderType == null){
            logger.error("Order converter error: orderType is null for {}", source);
        }

        SellOrder order =new SellOrder();;
        fill(order, source);
        return order;
    }

    private void fill(AbstractOrder order, DBObject source) {
        order.setPrimaryKey(source.get("_id").toString());
        order.setItemPrice(BigDecimal.valueOf((Double) source.get("itemPrice")).setScale(9));
        order.setTradeAmount(BigDecimal.valueOf((Double) source.get("tradeAmount")).setScale(9));
        order.setItemRemaining(BigDecimal.valueOf((Double) source.get("itemRemaining")).setScale(9));
        order.setPlaceDate((Date) source.get("placeDate"));
        order.setOrderBookId(new OrderBookId((String) source.get("orderBookId")));
        order.setOrderStatus(OrderStatus.valueOf((String) source.get("orderStatus")));
        order.setVersion(Long.valueOf((String) source.get("version")));

//        .start("_id", source.getPrimaryKey())
//                .append("_class", source.getClass().getName())
//                .append("itemPrice", source.getItemPrice().doubleValue())
//                .append("tradeAmount", source.getTradeAmount().doubleValue())
//                .append("itemRemaining", source.getItemRemaining().doubleValue())
//                .append("placeDate", source.getPlaceDate())
//                .append("orderType", source.getOrderType().toString())
//                .append("orderBookId", source.getOrderBookId().toString())
//                .append("orderStatus", source.getOrderStatus().toString())
//                .append("version", source.getVersion())
    }
}