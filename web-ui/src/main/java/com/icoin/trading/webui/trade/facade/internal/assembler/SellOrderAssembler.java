package com.icoin.trading.webui.trade.facade.internal.assembler;

import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
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
public class SellOrderAssembler {
    public SellOrder toDomain(com.icoin.trading.webui.order.SellOrder sellOrder) {
        final SellOrder order = new SellOrder();
        order.setTradeAmount(BigMoney.of(CurrencyUnit.of(sellOrder.getAmountCcy()),sellOrder.getTradeAmount()));
        //at first, amount is always equal to remaining amount
        order.setItemRemaining(BigMoney.of(CurrencyUnit.of(sellOrder.getAmountCcy()),sellOrder.getTradeAmount()));
        order.setItemPrice(BigMoney.of(CurrencyUnit.of(sellOrder.getPriceCcy()),sellOrder.getItemPrice()));
        order.setCurrencyPair(new CurrencyPair(sellOrder.getAmountCcy(),sellOrder.getPriceCcy()));

        return order;
    }
}
