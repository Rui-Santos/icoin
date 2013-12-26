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
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservedEvent;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class PortfolioItemEventListener {

    private final static Logger logger = LoggerFactory.getLogger(PortfolioItemEventListener.class);
    private PortfolioQueryRepository portfolioRepository;
    private CoinQueryRepository coinQueryRepository;

    @EventHandler
    public void handleEvent(ItemAddedToPortfolioEvent event) {
        logger.debug("Handle ItemAddedToPortfolioEvent for orderbook with primaryKey {}",
                event.getCoinId());
        ItemEntry itemEntry = createItemEntry(event.getCoinId().toString(), event.getAmountOfItemAdded());

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.addItemInPossession(itemEntry);

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservationCancelledForPortfolioEvent event) {
        logger.debug("Handle ItemReservationCancelledForPortfolioEvent for orderbook with primaryKey {}",
                event.getCoinId());
        ItemEntry itemEntry = createItemEntry(event.getCoinId().toString(),
                event.getLeftTotalItem());

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.removeReservedItem(itemEntry.getCoinIdentifier(), event.getLeftTotalItem());
        portfolioEntry.addItemInPossession(itemEntry);

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservationConfirmedForPortfolioEvent event) {
        logger.debug("Handle ItemReservationConfirmedForPortfolioEvent for orderbook with primaryKey {}",
                event.getCoinId());
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.removeReservedItem(event.getCoinId().toString(), event.getAmount());
        portfolioEntry.removeItemsInPossession(event.getCoinId().toString(),
                event.getAmount());

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservedEvent event) {
        logger.debug("Handle ItemReservedEvent for orderbook with primaryKey {}", event.getCoinId());
        ItemEntry itemEntry = createItemEntry(event.getCoinId().toString(),
                event.getAmountOfItemReserved());

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.addReservedItem(itemEntry);

        portfolioRepository.save(portfolioEntry);
    }

    private ItemEntry createItemEntry(String primaryKey, BigMoney amount) {
        CoinEntry coin = coinQueryRepository.findOne(primaryKey);
        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setCoinIdentifier(coin.getPrimaryKey());
        itemEntry.setCoinName(coin.getName());
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
    public void setCoinQueryRepository(CoinQueryRepository coinQueryRepository) {
        this.coinQueryRepository = coinQueryRepository;
    }
}
