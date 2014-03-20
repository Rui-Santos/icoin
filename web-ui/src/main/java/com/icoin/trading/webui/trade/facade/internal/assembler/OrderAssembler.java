package com.icoin.trading.webui.trade.facade.internal.assembler;

import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import com.icoin.trading.webui.order.AbstractOrder;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-22
 * Time: PM8:22
 * To change this template use File | Settings | File Templates.
 */
public class OrderAssembler {
    public Order toBuyOrder(BuyOrder buyOrder) {
        final Order order = new Order(OrderType.BUY);
        fillOrder(buyOrder, order);
        return order;
    }

    public Order toSellOrder(SellOrder sellOrder) {
        final Order order = new Order(OrderType.SELL);
        fillOrder(sellOrder, order);

        return order;
    }

    private void fillOrder(AbstractOrder abstractOrder, Order order) {
        order.setTradeAmount(BigMoney.of(CurrencyUnit.of(abstractOrder.getAmountCcy()), abstractOrder.getTradeAmount()));
        //at first, amount is always equal to remaining amount
        order.setItemRemaining(BigMoney.of(CurrencyUnit.of(abstractOrder.getAmountCcy()), abstractOrder.getTradeAmount()));
        order.setItemPrice(BigMoney.of(CurrencyUnit.of(abstractOrder.getPriceCcy()), abstractOrder.getItemPrice()));
        order.setCurrencyPair(new CurrencyPair(abstractOrder.getAmountCcy(), abstractOrder.getPriceCcy()));
    }
}
