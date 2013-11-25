/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.tradeengine.query.orderbook;

import com.icoin.trading.tradeengine.domain.events.coin.OrderBookAddedToCoinEvent;
import com.icoin.trading.tradeengine.domain.events.order.AbstractOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.BuyOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.SellOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
@Component
public class OrderBookListener {

    private static final String BUY = "Buy";
    private static final String SELL = "Sell";
    private BigDecimal lowestPrice = BigDecimal.valueOf(0.00000001);

    private OrderBookQueryRepository orderBookRepository;
    private CoinQueryRepository coinRepository;
    private TradeExecutedQueryRepository tradeExecutedRepository;

    @Value("${trade.lowestPrice}")
    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    @EventHandler
    public void handleOrderBookAddedToCoinEvent(OrderBookAddedToCoinEvent event) {
        CoinEntry coinEntry = coinRepository.findOne(event.getCoinId().toString());
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCoinIdentifier(event.getCoinId().toString());
        orderBookEntry.setCoinName(coinEntry.getName());
        orderBookEntry.setIdentifier(event.getOrderBookId().toString());
        orderBookRepository.save(orderBookEntry);
    }

    @EventHandler
    public void handleBuyOrderPlaced(BuyOrderPlacedEvent event) {
        OrderBookEntry orderBook = orderBookRepository.findOne(event.orderBookIdentifier().toString());

        OrderEntry buyOrder = createPlacedOrder(event, BUY);
        orderBook.buyOrders().add(buyOrder);

        orderBookRepository.save(orderBook);
    }

    @EventHandler
    public void handleSellOrderPlaced(SellOrderPlacedEvent event) {
        OrderBookEntry orderBook = orderBookRepository.findOne(event.orderBookIdentifier().toString());

        OrderEntry sellOrder = createPlacedOrder(event, SELL);
        orderBook.sellOrders().add(sellOrder);

        orderBookRepository.save(orderBook);
    }

    @EventHandler
    public void handleTradeExecuted(TradeExecutedEvent event) {
        OrderId buyOrderId = event.getBuyOrderId();
        OrderId sellOrderId = event.getSellOrderId();

        OrderBookId orderBookIdentifier = event.getOrderBookIdentifier();
        OrderBookEntry orderBookEntry = orderBookRepository.findOne(orderBookIdentifier.toString());

        TradeExecutedEntry tradeExecutedEntry = new TradeExecutedEntry();
        tradeExecutedEntry.setCoinName(orderBookEntry.getCoinName());
        tradeExecutedEntry.setOrderBookIdentifier(orderBookEntry.getIdentifier());
        tradeExecutedEntry.setTradeAmount(event.getTradeAmount());
        tradeExecutedEntry.setTradePrice(event.getTradePrice());

        tradeExecutedRepository.save(tradeExecutedEntry);

        // TODO find a better solution or maybe pull them apart
        OrderEntry foundBuyOrder = null;
        for (OrderEntry order : orderBookEntry.buyOrders()) {
            if (order.getIdentifier().equals(buyOrderId.toString())) {
                BigDecimal itemsRemaining = order.getItemsRemaining();
                order.setItemsRemaining(itemsRemaining.subtract(event.getTradeAmount()));
                foundBuyOrder = order;
                break;
            }
        }
        if (null != foundBuyOrder && foundBuyOrder.getItemsRemaining().compareTo(lowestPrice)<0) {
            orderBookEntry.buyOrders().remove(foundBuyOrder);
        }
        OrderEntry foundSellOrder = null;
        for (OrderEntry order : orderBookEntry.sellOrders()) {
            if (order.getIdentifier().equals(sellOrderId.toString())) {
                BigDecimal itemsRemaining = order.getItemsRemaining();
                order.setItemsRemaining(itemsRemaining.subtract(event.getTradeAmount()));
                foundSellOrder = order;
                break;
            }
        }
        if (null != foundSellOrder && foundSellOrder.getItemsRemaining().compareTo(lowestPrice)<0) {
            orderBookEntry.sellOrders().remove(foundSellOrder);
        }
        orderBookRepository.save(orderBookEntry);
    }

    private OrderEntry createPlacedOrder(AbstractOrderPlacedEvent event, String type) {
        OrderEntry entry = new OrderEntry();
        entry.setIdentifier(event.getOrderId().toString());
        entry.setItemsRemaining(event.getTradeAmount());
        entry.setTradeAmount(event.getTradeAmount());
        entry.setUserId(event.getPortfolioId().toString());
        entry.setType(type);
        entry.setItemPrice(event.getItemPrice());

        return entry;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderBookRepository(OrderBookQueryRepository orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setCoinRepository(CoinQueryRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setTradeExecutedRepository(TradeExecutedQueryRepository tradeExecutedRepository) {
        this.tradeExecutedRepository = tradeExecutedRepository;
    }
}
