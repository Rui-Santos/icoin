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

import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemReservedEvent;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
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
        if (logger.isDebugEnabled()) {
            logger.debug("Handle ItemAddedToPortfolioEvent {} for coin {} with amount {}", event.getPortfolioIdentifier(),
                    event.getCoinId(), event.getAmountOfItemAdded());
        }
        CoinEntry coin = findCoinEntry(event.getCoinId());

        if (coin == null) {
            logger.error("coin {} cannot be found.", event.getCoinId());
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());

        if (!portfolioEntry.hasItem(event.getCoinId().toString())) {
            portfolioEntry.createItem(coin.getPrimaryKey(), coin.getName());
        }

        portfolioEntry.addItemInPossession(coin.getPrimaryKey(), event.getAmountOfItemAdded());

        portfolioRepository.save(portfolioEntry);
    }

    private CoinEntry findCoinEntry(CoinId coinId) {
        return coinQueryRepository.findOne(coinId.toString());
    }

    @EventHandler
    public void handleEvent(ItemReservationCancelledForPortfolioEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle ItemReservationCancelledForPortfolioEvent {} for coin {}, left commission {}, left total item {}", event.getPortfolioIdentifier(),
                    event.getCoinId(), event.getLeftCommission(), event.getLeftTotalItem());
        }
        CoinEntry coin = findCoinEntry(event.getCoinId());

        if (coin == null) {
            logger.error("coin {} cannot be found.", event.getCoinId());
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.cancelReserved(coin.getPrimaryKey(), event.getLeftTotalItem().plus(event.getLeftCommission()));

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservationConfirmedForPortfolioEvent event) {
        logger.debug("Handle ItemReservationConfirmedForPortfolioEvent {} for coin {}, amount {}, commission {}", event.getPortfolioIdentifier(),
                event.getCoinId(), event.getAmount(), event.getCommission());
        CoinEntry coin = findCoinEntry(event.getCoinId());

        if (coin == null) {
            logger.error("coin {} cannot be found.", event.getCoinId());
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.confirmReserved(event.getCoinId().toString(), event.getAmount().plus(event.getCommission()));

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(ItemReservedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle ItemReservedEvent {} for coin {}, amount {}", event.getPortfolioIdentifier(), event.getCoinId(), event.getAmountOfItemReserved());
        }
        final CoinEntry coin = findCoinEntry(event.getCoinId());

        if (coin == null) {
            logger.error("coin {} cannot be found.", event.getCoinId());
            return;
        }

        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.addReserved(coin.getPrimaryKey(), event.getAmountOfItemReserved());

        portfolioRepository.save(portfolioEntry);
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
