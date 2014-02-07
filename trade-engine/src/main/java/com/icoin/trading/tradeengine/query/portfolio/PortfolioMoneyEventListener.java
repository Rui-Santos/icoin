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

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.events.portfolio.PortfolioCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
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
public class PortfolioMoneyEventListener {

    private final static Logger logger = LoggerFactory.getLogger(PortfolioMoneyEventListener.class);

    private PortfolioQueryRepository portfolioRepository;
    private UserQueryRepository userQueryRepository;

    @EventHandler
    public void handleEvent(PortfolioCreatedEvent event) {
        logger.debug("About to handle the PortfolioCreatedEvent for user with primaryKey {}",
                event.getUserId().toString());

        PortfolioEntry portfolioEntry = new PortfolioEntry();
        portfolioEntry.setIdentifier(event.getPortfolioId().toString());
        portfolioEntry.setUserIdentifier(event.getUserId().toString());
        final UserEntry user = userQueryRepository.findOne(event.getUserId().toString());
        portfolioEntry.setUserName(user.getUserName());
        portfolioEntry.setFullName(user.getFullName());
        portfolioEntry.setAmountOfMoney(BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT));
        portfolioEntry.setReservedAmountOfMoney(BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT));

        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashDepositedEvent event) {
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.setAmountOfMoney(portfolioEntry.getAmountOfMoney().plus(event.getMoneyAdded()));
        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashWithdrawnEvent event) {
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        portfolioEntry.setAmountOfMoney(portfolioEntry.getAmountOfMoney().minus(event.getAmountPaid()));
        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashReservedEvent event) {
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        final BigMoney total = event.getTotalMoney().plus(event.getTotalCommission());
        portfolioEntry.setReservedAmountOfMoney(portfolioEntry.getReservedAmountOfMoney().plus(total));
        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashReservationCancelledEvent event) {
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        final BigMoney totalLeft = event.getLeftTotalMoney().plus(event.getLeftCommission());
        portfolioEntry.setReservedAmountOfMoney(
                portfolioEntry.getReservedAmountOfMoney().minus(totalLeft));

//        portfolioEntry.setAmountOfMoney(portfolioEntry.getAmountOfMoney().plus(totalLeft));
        portfolioRepository.save(portfolioEntry);
    }

    @EventHandler
    public void handleEvent(CashReservationConfirmedEvent event) {
        PortfolioEntry portfolioEntry = portfolioRepository.findOne(event.getPortfolioIdentifier().toString());
        BigMoney reservedAmountOfMoney = portfolioEntry.getReservedAmountOfMoney();
        BigMoney amountOfMoneyConfirmed = event.getAmountOfMoney().plus(event.getCommission());
        if (amountOfMoneyConfirmed.compareTo(reservedAmountOfMoney) < 0) {
            portfolioEntry.setReservedAmountOfMoney(reservedAmountOfMoney.minus(amountOfMoneyConfirmed));
        } else {
            portfolioEntry.setReservedAmountOfMoney(BigMoney.zero(event.getAmountOfMoney().getCurrencyUnit()));
        }

        portfolioEntry.setAmountOfMoney(portfolioEntry.getAmountOfMoney().minus(amountOfMoneyConfirmed));
        portfolioRepository.save(portfolioEntry);
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioRepository(PortfolioQueryRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserQueryRepository(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }
}
