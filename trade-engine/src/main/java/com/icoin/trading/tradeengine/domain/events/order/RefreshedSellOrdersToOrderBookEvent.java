package com.icoin.trading.tradeengine.domain.events.order;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-2
 * Time: PM11:08
 * To change this template use File | Settings | File Templates.
 */
public class RefreshedSellOrdersToOrderBookEvent
        extends ValueObjectSupport<RefreshedSellOrdersToOrderBookEvent> {
    private OrderBookId orderBookId;
    private CurrencyPair currencyPair;
    private CoinId coinId;
    private List<SellOrder> prioritySellOrders;

    public RefreshedSellOrdersToOrderBookEvent(OrderBookId orderBookId,
                                               CurrencyPair currencyPair,
                                               CoinId coinId,
                                               List<SellOrder> prioritySellOrders) {
        this.orderBookId = orderBookId;
        this.currencyPair = currencyPair;
        this.coinId = coinId;
        this.prioritySellOrders = prioritySellOrders;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public List<SellOrder> getPrioritySellOrders() {
        return prioritySellOrders;
    }
}
