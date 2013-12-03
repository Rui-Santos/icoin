package com.icoin.trading.tradeengine.domain.model.order;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-2
 * Time: PM10:40
 * To change this template use File | Settings | File Templates.
 */
public class SellOrder extends AbstractOrder<SellOrder> {
    public SellOrder() {
        super(OrderType.SELL);
    }
}
