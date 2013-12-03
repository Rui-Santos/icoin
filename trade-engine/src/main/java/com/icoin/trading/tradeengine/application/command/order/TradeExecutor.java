package com.icoin.trading.tradeengine.application.command.order;

import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-3
 * Time: AM8:02
 * To change this template use File | Settings | File Templates.
 */
public interface TradeExecutor {
   <T extends AbstractOrder> void put(T element);
}
