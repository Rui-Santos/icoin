package com.icoin.trading.tradeengine.domain.events.order;

import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import org.joda.money.BigMoney;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-4
 * Time: AM9:10
 * To change this template use File | Settings | File Templates.
 */
public class RefreshedLowestSellPriceEvent extends AbstractRefreshedPriceEvent<RefreshedLowestSellPriceEvent> {
    private String lowestSellOrderId;

    public RefreshedLowestSellPriceEvent(OrderBookId orderBookId, String lowestSellOrderId, BigMoney price) {
        super(orderBookId, price);
        this.lowestSellOrderId = lowestSellOrderId;
    }

    public String getLowestSellOrderId() {
        return lowestSellOrderId;
    }
}
