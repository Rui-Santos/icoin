package com.icoin.trading.tradeengine.query.tradeexecuted;

import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-5
 * Time: AM12:31
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TradeExecutedListener {
    private OrderBookQueryRepository orderBookRepository;
    private TradeExecutedQueryRepository tradeExecutedRepository;

    @EventHandler
    public void handleTradeExecuted(TradeExecutedEvent event) {
        OrderBookId orderBookIdentifier = event.getOrderBookIdentifier();
        OrderBookEntry orderBookEntry = orderBookRepository.findOne(orderBookIdentifier.toString());

        TradeExecutedEntry tradeExecutedEntry = new TradeExecutedEntry();
        tradeExecutedEntry.setCoinName(orderBookEntry.getCoinName());
        tradeExecutedEntry.setOrderBookIdentifier(orderBookEntry.getPrimaryKey());
        tradeExecutedEntry.setTradedAmount(event.getTradeAmount());
        tradeExecutedEntry.setTradedPrice(event.getTradedPrice());
        tradeExecutedEntry.setTradeTime(event.getTradeTime());
        tradeExecutedEntry.setTradeType(TradeType.convert(event.getTradeType()));

        tradeExecutedRepository.save(tradeExecutedEntry);
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderBookRepository(OrderBookQueryRepository orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setTradeExecutedRepository(TradeExecutedQueryRepository tradeExecutedRepository) {
        this.tradeExecutedRepository = tradeExecutedRepository;
    }
}
