package com.icoin.trading.api.tradeengine.events.order;

import com.icoin.trading.api.tradeengine.events.order.OrderBookId;
import org.joda.money.BigMoney;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-4
 * Time: AM9:10
 * To change this template use File | Settings | File Templates.
 */
public class RefreshedHighestBuyPriceEvent extends AbstractRefreshedPriceEvent<RefreshedHighestBuyPriceEvent> {
    private String highestBuyOrderId;

    public RefreshedHighestBuyPriceEvent(OrderBookId orderBookId, String highestBuyOrderId, BigMoney price) {
        super(orderBookId, price);
        this.highestBuyOrderId = highestBuyOrderId;
    }

    public String getHighestBuyOrderId() {
        return highestBuyOrderId;
    }
}
