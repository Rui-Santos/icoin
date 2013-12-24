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

package com.icoin.trading.tradeengine.application.command.transaction;

import com.icoin.trading.tradeengine.application.Callback;
import com.icoin.trading.tradeengine.application.SynchronizedOnIdentifierHandler;
import com.icoin.trading.tradeengine.application.command.transaction.command.CancelTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ConfirmTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ExecutedTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.transaction.Transaction;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionType;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class TransactionCommandHandler {
    private final SynchronizedOnIdentifierHandler synchronizedOnIdentifierHandler = new SynchronizedOnIdentifierHandler();
    private Repository<Transaction> repository;

    @CommandHandler
    public void handleStartBuyTransactionCommand(StartBuyTransactionCommand command) {
        Transaction transaction =
                new Transaction(
                        command.getTransactionIdentifier(),
                        TransactionType.BUY,
                        command.getOrderbookIdentifier(),
                        command.getPortfolioIdentifier(),
                        command.getTradeAmount(),
                        command.getItemPrice());
        repository.add(transaction);
    }

    @CommandHandler
    public void handleStartSellTransactionCommand(StartSellTransactionCommand command) {
        Transaction transaction =
                new Transaction(
                        command.getTransactionIdentifier(),
                        TransactionType.SELL,
                        command.getOrderbookIdentifier(),
                        command.getPortfolioIdentifier(),
                        command.getTradeAmount(),
                        command.getItemPrice());
        repository.add(transaction);
    }

    @CommandHandler
    public void handleConfirmTransactionCommand(final ConfirmTransactionCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getTransactionIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Transaction transaction = repository.load(command.getTransactionIdentifier());
                        transaction.confirm(command.getConfirmDate());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleCancelTransactionCommand(final CancelTransactionCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getTransactionIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Transaction transaction = repository.load(command.getTransactionIdentifier());
                        transaction.cancel(command.getCancelledPrice());
                        return null;
                    }
                }
        );
    }

    @CommandHandler
    public void handleExecutedTransactionCommand(final ExecutedTransactionCommand command) {
        synchronizedOnIdentifierHandler.perform(
                new Callback<Void>() {
                    @Override
                    public String getIdentifier() {
                        return command.getTransactionIdentifier().toString();
                    }

                    @Override
                    public Void execute() throws Exception {
                        Transaction transaction = repository.load(command.getTransactionIdentifier());
                        transaction.execute(command.getAmountOfItems(), command.getItemPrice());
                        return null;
                    }
                }
        );
    }

    @Autowired
    @Qualifier("transactionRepository")
    public void setRepository(Repository<Transaction> repository) {
        this.repository = repository;
    }
}
