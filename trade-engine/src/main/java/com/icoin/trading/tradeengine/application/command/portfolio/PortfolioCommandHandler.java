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

import com.icoin.trading.tradeengine.application.Callback;
import com.icoin.trading.tradeengine.application.SynchronizedOnIdentifierHandler;
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
    private final SynchronizedOnIdentifierHandler synchronizedOnIdentifierHandler = new SynchronizedOnIdentifierHandler();
    private Repository<Portfolio> portfolioRepository;

    @CommandHandler
    public void handleCreatePortfolio(CreatePortfolioCommand command) {
        Portfolio portfolio = new Portfolio(command.getPortfolioId(), command.getUserId());
        portfolioRepository.add(portfolio);
    }

    @CommandHandler
    public void handleReserveItemsCommand(final ReserveAmountCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.reserveItems(command.getCoinId(),
                                command.getTransactionIdentifier(),
                                command.getAmountOfItemToReserve());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleAddItemsToPortfolioCommand(final AddAmountToPortfolioCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.addItems(command.getCoinId(), command.getAmountOfItemToAdd());
                        return null;
                    }
                }
        );

    }

    @CommandHandler
    public void handleConfirmReservationCommand(final ConfirmAmountReservationForPortfolioCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.confirmReservation(command.getCoinId(),
                                command.getTransactionIdentifier(),
                                command.getAmountOfItemToConfirm());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleCancelReservationCommand(final CancelAmountReservationForPortfolioCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.cancelReservation(command.getCoinId(),
                                command.getTransactionIdentifier(),
                                command.getAmountOfItemsToCancel());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleAddMoneyToPortfolioCommand(final DepositCashCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.addMoney(command.getMoneyToAdd());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleMakePaymentFromPortfolioCommand(final WithdrawCashCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.makePayment(command.getAmountToPay());
                        return null;
                    }
                }
        );

    }

    @CommandHandler
    public void handleReserveMoneyFromPortfolioCommand(final ReserveCashCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.reserveMoney(command.getTransactionIdentifier(), command.getAmountOfMoneyToReserve());
                        return null;
                    }
                }
        );

    }

    @CommandHandler
    public void handleCancelMoneyReservationFromPortfolioCommand(final CancelCashReservationCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.cancelMoneyReservation(command.getTransactionIdentifier(), command.getAmountOfMoneyToCancel());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleConfirmMoneyReservationFromPortfolioCommand(final ConfirmCashReservationCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());
                        portfolio.confirmMoneyReservation(command.getTransactionIdentifier(),
                                command.getAmountOfMoneyToConfirm());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleAddBackLeftReservedCommand(final AddBackLeftReservedCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());

                        //todo add money back
//        portfolio.confirmMoneyReservation(command.getTransactionIdentifier(),
//                command.getAmountOfMoneyToConfirm());
                        return null;
                    }
                }
        );

    }

    @Autowired
    @Qualifier("portfolioRepository")
    public void setRepository(Repository<Portfolio> repository) {
        this.portfolioRepository = repository;
    }
}
