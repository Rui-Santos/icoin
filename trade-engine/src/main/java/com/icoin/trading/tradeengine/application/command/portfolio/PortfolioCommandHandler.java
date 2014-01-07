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
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ClearReservedCashCommand;
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
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class PortfolioCommandHandler {
    private static Logger logger = LoggerFactory.getLogger(PortfolioCommandHandler.class);
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
                        portfolio.reserveItem(command.getCoinId(),
                                command.getTransactionIdentifier(),
                                command.getAmountOfItemToReserve(),
                                command.getCommission());
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
                        portfolio.addItem(command.getCoinId(), command.getAmountOfItemToAdd());
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
                                command.getAmountOfItem(),
                                command.getCommission());
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
                                command.getLeftTotalItem(),
                                command.getLeftCommission());
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
                        portfolio.reserveMoney(command.getTransactionIdentifier(), command.getTotalMoney() ,command.getTotalCommission());
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
                        portfolio.cancelMoneyReservation(
                                command.getTransactionIdentifier(),
                                command.getLeftTotalMoney(),
                                command.getLeftCommission());
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
                        portfolio.confirmMoneyReservation(
                                command.getTransactionIdentifier(),
                                command.getAmountOfMoney(),
                                command.getCommission());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleClearReservedCashCommand(final ClearReservedCashCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getPortfolioIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Portfolio portfolio = portfolioRepository.load(command.getPortfolioIdentifier());

                        //add left back
                        final BigMoney moneyToClear = command.getLeftReservedMoney().plus(command.getLeftCommission());

                        logger.info("Clear reserved total {}, amount {}, commission {} for transaction {}, portfolio {}",
                                moneyToClear, command.getLeftReservedMoney(), command.getLeftCommission(),
                                command.getTransactionIdentifier(), command.getPortfolioIdentifier());
                        portfolio.clearReservedMoney(command.getTransactionIdentifier(), moneyToClear);
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
