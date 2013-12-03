package com.icoin.trading.tradeengine.domain.model.order;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-2
 * Time: PM10:40
 * To change this template use File | Settings | File Templates.
 */
public class BuyOrder extends AbstractOrder<BuyOrder> {
    public BuyOrder() {
        super(OrderType.BUY);
    }
}
