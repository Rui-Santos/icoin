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
import com.icoin.trading.tradeengine.application.command.transaction.command.AbstractStartTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.CancelTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ConfirmTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ExecutedTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.commission.Commission;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.transaction.Transaction;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionType;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.money.BigMoney;
import org.joda.money.Money;
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
    private CommissionPolicyFactory commissionPolicyFactory;

    @CommandHandler
    public void handleStartBuyTransactionCommand(StartBuyTransactionCommand command) {
        final BuyOrder order = toOrder(command);

        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(order);
        Commission commission = commissionPolicy.calculateBuyCommission(order);

        final BigMoney totalCommission = commission.getBigMoneyCommission();

        Transaction transaction =
                new Transaction(
                        command.getTransactionIdentifier(),
                        command.getCoinId(),
                        TransactionType.BUY,
                        command.getOrderBookIdentifier(),
                        command.getPortfolioIdentifier(),
                        command.getTradeAmount(),
                        command.getItemPrice(),
                        totalCommission);
        repository.add(transaction);
    }

    @CommandHandler
    public void handleStartSellTransactionCommand(StartSellTransactionCommand command) {
        final SellOrder order = toOrder(command);

        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(order);
        Commission commission = commissionPolicy.calculateSellCommission(order);

        final BigMoney totalCommission = commission.getBigMoneyCommission();

        Transaction transaction =
                new Transaction(
                        command.getTransactionIdentifier(),
                        command.getCoinId(),
                        TransactionType.SELL,
                        command.getOrderBookIdentifier(),
                        command.getPortfolioIdentifier(),
                        command.getTradeAmount(),
                        command.getItemPrice(),
                        totalCommission);
        repository.add(transaction);
    }

    private BuyOrder toOrder(StartBuyTransactionCommand command) {
        final BuyOrder order = new BuyOrder();
        fillOrder(command, order);
        return order;
    }

    private SellOrder toOrder(StartSellTransactionCommand command) {
        final SellOrder order = new SellOrder();
        fillOrder(command, order);
        return order;
    }

    private void fillOrder(AbstractStartTransactionCommand command, AbstractOrder order) {
        order.setItemRemaining(command.getTradeAmount());
        order.setItemPrice(command.getItemPrice());
        order.setPortfolioId(command.getPortfolioIdentifier());
        order.setOrderBookId(command.getOrderBookIdentifier());
        order.setCurrencyPair(command.getCurrencyPair());
        order.setCoinId(command.getCoinId());
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
                        transaction.execute(command.getAmountOfItems(), command.getItemPrice(), command.getCommission());
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

    @Autowired
    public void setCommissionPolicyFactory(CommissionPolicyFactory commissionPolicyFactory) {
        this.commissionPolicyFactory = commissionPolicyFactory;
    }
}
