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

package com.icoin.trading.tradeengine.query.order;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.events.order.AbstractOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.BuyOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.order.SellOrderPlacedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
@Component
public class OrderListener {
    private static final String BUY = "Buy";
    private static final String SELL = "Sell";
    private BigDecimal lowestPrice = Constants.IGNORED_PRICE;

    private OrderQueryRepository orderRepository;

    //@Value("#{trading.lowestTradePrice}")
    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = BigDecimal.valueOf(lowestPrice);
    }

    @EventHandler
    public void handleBuyOrderPlaced(BuyOrderPlacedEvent event) {
        OrderEntry buyOrder = createPlacedOrder(event, BUY);
        orderRepository.save(buyOrder);
    }

    @EventHandler
    public void handleSellOrderPlaced(SellOrderPlacedEvent event) {
        OrderEntry sellOrder = createPlacedOrder(event, SELL);
        orderRepository.save(sellOrder);
    }

    @EventHandler
    public void handleTradeExecuted(TradeExecutedEvent event) {
        String buyOrderId = event.getBuyOrderId();
        String sellOrderId = event.getSellOrderId();

        final OrderEntry buyOrder = orderRepository.findOne(buyOrderId);
        buyOrder.recordTraded(event.getTradeAmount(), event.getTradeTime());
        orderRepository.save(buyOrder);

        final OrderEntry sellOrder = orderRepository.findOne(sellOrderId);
        sellOrder.recordTraded(event.getTradeAmount(), event.getTradeTime());
        orderRepository.save(sellOrder);
    }

    private OrderEntry createPlacedOrder(AbstractOrderPlacedEvent event, String type) {
        OrderEntry entry = new OrderEntry();
        entry.setPrimaryKey(event.getOrderId().toString());
        entry.setOrderBookIdentifier(event.orderBookIdentifier().toString());
        entry.setItemsRemaining(event.getTradeAmount());
        entry.setTradeAmount(event.getTradeAmount());
        entry.setUserId(event.getPortfolioId().toString());
        entry.setType(type);
        entry.setItemPrice(event.getItemPrice());
        entry.setCoinExchangePair(event.getCoinExchangePair());

        return entry;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderRepository(OrderQueryRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
