package com.icoin.trading.webui.trade.facade.internal.assembler;

import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-22
 * Time: PM8:22
 * To change this template use File | Settings | File Templates.
 */
public class BuyOrderAssembler {
    public BuyOrder toDomain(com.icoin.trading.webui.order.BuyOrder buyOrder) {
        final BuyOrder order = new BuyOrder();
        order.setTradeAmount(BigMoney.of(CurrencyUnit.of(buyOrder.getAmountCcy()), buyOrder.getTradeAmount()));
        //at first, amount is always equal to remaining amount
        order.setItemRemaining(BigMoney.of(CurrencyUnit.of(buyOrder.getAmountCcy()), buyOrder.getTradeAmount()));
        order.setItemPrice(BigMoney.of(CurrencyUnit.of(buyOrder.getPriceCcy()), buyOrder.getItemPrice()));
        order.setCurrencyPair(new CurrencyPair(buyOrder.getAmountCcy(),buyOrder.getPriceCcy()));

        return order;
    }
}
