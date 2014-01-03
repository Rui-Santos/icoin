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

import com.icoin.trading.tradeengine.domain.events.coin.OrderBookAddedToCoinEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedHighestBuyPriceEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedLowestSellPriceEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class OrderBookListener {

    private OrderBookQueryRepository orderBookRepository;
    private CoinQueryRepository coinRepository;


    @EventHandler
    public void handleOrderBookAddedToCoinEvent(OrderBookAddedToCoinEvent event) {
        CoinEntry coinEntry = coinRepository.findOne(event.getCoinId().toString());
        OrderBookEntry orderBookEntry = new OrderBookEntry();
        orderBookEntry.setCoinIdentifier(event.getCoinId().toString());
        orderBookEntry.setCoinName(coinEntry.getName());
        orderBookEntry.setCurrencyPair(event.getCurrencyPair());
        orderBookEntry.setPrimaryKey(event.getOrderBookId().toString());
        orderBookRepository.save(orderBookEntry);
    }

    @EventHandler
    public void handleRefreshedHighestBuyPrice(RefreshedHighestBuyPriceEvent event) {
        OrderBookEntry orderBook = orderBookRepository.findOne(event.getOrderBookId().toString());
        orderBook.setHighestBuyId(event.getHighestBuyOrderId());
        orderBook.setHighestBuyPrice(event.getPrice());
        orderBookRepository.save(orderBook);
    }

    @EventHandler
    public void handleRefreshedLowestSellPrice(RefreshedLowestSellPriceEvent event) {
        OrderBookEntry orderBook = orderBookRepository.findOne(event.getOrderBookId().toString());
        orderBook.setLowestSellId(event.getLowestSellOrderId());
        orderBook.setLowestSellPrice(event.getPrice());
        orderBookRepository.save(orderBook);
    }

    @EventHandler
    public void handleTradeExecuted(TradeExecutedEvent event) {
        OrderBookId orderBookIdentifier = event.getOrderBookIdentifier();
        OrderBookEntry orderBookEntry = orderBookRepository.findOne(orderBookIdentifier.toString());

        orderBookEntry.setTradedPrice(event.getTradedPrice());
        orderBookEntry.setBuyTransactionId(event.getBuyTransactionId().toString());
        orderBookEntry.setSellTransactionId(event.getSellTransactionId().toString());
        orderBookEntry.setLastTradedTime(event.getTradeTime());
        orderBookRepository.save(orderBookEntry);
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
}
