package com.icoin.trading.tradeengine.application.command.order;

import com.icoin.trading.tradeengine.domain.model.order.Order;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TradeExecutor {
    void execute(Order element);

    void reinitialize();
}
