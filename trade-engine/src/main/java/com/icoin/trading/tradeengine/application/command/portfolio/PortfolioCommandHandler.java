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

package com.icoin.trading.tradeengine.application.command.portfolio;

import com.icoin.trading.tradeengine.application.command.portfolio.cash.CancelCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ConfirmCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ReserveCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.WithdrawCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.CancelAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ConfirmAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ReserveAmountCommand;
import com.icoin.trading.tradeengine.domain.model.portfolio.Portfolio;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class PortfolioCommandHandler {

    private Repository<Portfolio> portfolioRepository;

    @CommandHandler
    public void handleCreatePortfolio(CreatePortfolioCommand command) {
        Portfolio portfolio = new Portfolio(command.getPortfolioId(), command.getUserId());
        portfolioRepository.add(portfolio);
    }

    @CommandHandler
    public void handleReserveItemsCommand(ReserveAmountCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.reserveItems(command.getOrderBookIdentifier(),
                command.getTransactionIdentifier(),
                command.getAmountOfItemsToReserve());
    }

    @CommandHandler
    public void handleAddItemsToPortfolioCommand(AddAmountToPortfolioCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.addItems(command.getOrderBookIdentifier(), command.getAmountOfItemsToAdd());
    }

    @CommandHandler
    public void handleConfirmReservationCommand(ConfirmAmountReservationForPortfolioCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.confirmReservation(command.getOrderBookIdentifier(),
                command.getTransactionIdentifier(),
                command.getAmountOfItemsToConfirm());
    }

    @CommandHandler
    public void handleCancelReservationCommand(CancelAmountReservationForPortfolioCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.cancelReservation(command.getOrderBookIdentifier(),
                command.getTransactionIdentifier(),
                command.getAmountOfItemsToCancel());
    }

    @CommandHandler
    public void handleAddMoneyToPortfolioCommand(DepositCashCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.addMoney(command.getMoneyToAdd());
    }

    @CommandHandler
    public void handleMakePaymentFromPortfolioCommand(WithdrawCashCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.makePayment(command.getAmountToPayInCents());
    }

    @CommandHandler
    public void handleReserveMoneyFromPortfolioCommand(ReserveCashCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.reserveMoney(command.getTransactionIdentifier(), command.getAmountOfMoneyToReserve());
    }

    @CommandHandler
    public void handleCancelMoneyReservationFromPortfolioCommand(CancelCashReservationCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.cancelMoneyReservation(command.getTransactionIdentifier(), command.getAmountOfMoneyToCancel());
    }

    @CommandHandler
    public void handleConfirmMoneyReservationFromPortfolioCommand(
            ConfirmCashReservationCommand command) {
        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
        portfolio.confirmMoneyReservation(command.getTransactionIdentifier(),
                command.getAmountOfMoneyToConfirm());
    }

    @Autowired
    @Qualifier("portfolioRepository")
    public void setRepository(Repository<Portfolio> repository) {
        this.portfolioRepository = repository;
    }
}
