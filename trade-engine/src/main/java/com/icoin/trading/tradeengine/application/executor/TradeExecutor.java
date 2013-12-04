package com.icoin.trading.tradeengine.application.executor;

import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TradeExecutor {
    <T extends AbstractOrder> void execute(T element);
}
