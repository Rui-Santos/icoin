package com.icoin.trading.tradeengine.application.command.order;

import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/3/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OrderExecutor<T extends AbstractOrder> {
    void execute(T element);
}