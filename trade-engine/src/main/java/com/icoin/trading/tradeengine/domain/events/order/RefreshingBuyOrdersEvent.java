package com.icoin.trading.tradeengine.domain.events.order;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinExchangePair;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-2
 * Time: PM11:08
 * To change this template use File | Settings | File Templates.
 */
public class RefreshingBuyOrdersEvent
        extends ValueObjectSupport<RefreshingBuyOrdersEvent> {
    private OrderBookId orderBookId;
    private CoinExchangePair coinExchangePair;
    private CoinId coinId;
    private List<BuyOrder> priorityBuyOrders;

    public RefreshingBuyOrdersEvent(OrderBookId orderBookId,
                                    CoinExchangePair coinExchangePair,
                                    CoinId coinId,
                                    List<BuyOrder> priorityBuyOrders) {
        this.orderBookId = orderBookId;
        this.coinExchangePair = coinExchangePair;
        this.coinId = coinId;
        this.priorityBuyOrders = priorityBuyOrders;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public CoinExchangePair getCoinExchangePair() {
        return coinExchangePair;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public List<BuyOrder> getPriorityBuyOrders() {
        return priorityBuyOrders;
    }
}
