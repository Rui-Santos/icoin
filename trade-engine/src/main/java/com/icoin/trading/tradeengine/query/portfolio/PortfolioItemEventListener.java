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

package com.icoin.trading.tradeengine.query.portfolio;

import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsReservedEvent;
import com.icoin.trading.tradeengine.query.orderbook.OrderBookEntry;
import com.icoin.trading.tradeengine.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
@Component
public class PortfolioItemEventListener {

    private final static Logger logger = LoggerFactory.getLogger(PortfolioItemEventListener.class);
    private PortfolioQueryRepository portfolioRepository;
    private OrderBookQueryRepository orderBookQueryRepository;

    @EventHandler
    public void handleEvent(ItemsAddedToPortfolioEvent event) {
        logger.debug("Handle ItemsAddedToPortfolioEvent for orderbook with primaryKey {}",
                event.getOrderBookIdentifier());
        ItemEntry itemEntry = createItemEntry(event.getOrderBookIdentifier().toString(), event.getAmountOfItemsAdded());

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.addItemInPossession(itemEntry);

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservationCancelledForPortfolioEvent event) {
        logger.debug("Handle ItemReservationCancelledForPortfolioEvent for orderbook with primaryKey {}",
                event.getOrderBookIdentifier());
        ItemEntry itemEntry = createItemEntry(event.getOrderBookIdentifier().toString(),
                event.getAmountOfCancelledAmount());

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.removeReservedItem(event.getOrderBookIdentifier().toString(), event.getAmountOfCancelledAmount());
        portfolioEntry.addItemInPossession(itemEntry);

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservationConfirmedForPortfolioEvent event) {
        logger.debug("Handle ItemReservationConfirmedForPortfolioEvent for orderbook with primaryKey {}",
                event.getOrderBookIdentifier());
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.removeReservedItem(event.getOrderBookIdentifier().toString(), event.getAmountOfConfirmedItems());
        portfolioEntry.removeItemsInPossession(event.getOrderBookIdentifier().toString(),
                event.getAmountOfConfirmedItems());

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemsReservedEvent event) {
        logger.debug("Handle ItemsReservedEvent for orderbook with primaryKey {}", event.getOrderBookIdentifier());
        ItemEntry itemEntry = createItemEntry(event.getOrderBookIdentifier().toString(),
                event.getAmountOfItemsReserved());

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.addReservedItem(itemEntry);

        portfolioRepository.save(portfolioEntry);
    }

    private ItemEntry createItemEntry(String primaryKey, BigDecimal amount) {
        OrderBookEntry orderBookEntry = orderBookQueryRepository.findOne(primaryKey);
        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setPrimaryKey(primaryKey);
        itemEntry.setCoinIdentifier(orderBookEntry.getCoinIdentifier());
        itemEntry.setCoinName(orderBookEntry.getCoinName());
        itemEntry.setAmount(amount);
        return itemEntry;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioRepository(PortfolioQueryRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderBookQueryRepository(OrderBookQueryRepository orderBookQueryRepository) {
        this.orderBookQueryRepository = orderBookQueryRepository;
    }
}
