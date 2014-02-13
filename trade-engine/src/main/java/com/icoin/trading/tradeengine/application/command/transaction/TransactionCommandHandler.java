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
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import com.icoin.trading.tradeengine.domain.model.transaction.Transaction;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionType;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.money.BigMoney;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

import static com.homhon.util.Asserts.notNull;

/**
 * @author Jettro Coenradie
 */
@Component
public class TransactionCommandHandler {
    private final SynchronizedOnIdentifierHandler synchronizedOnIdentifierHandler = new SynchronizedOnIdentifierHandler();
    private Repository<Transaction> repository;
    private CommissionPolicyFactory commissionPolicyFactory;

    @SuppressWarnings("unused")
    @CommandHandler
    public void handleStartBuyTransactionCommand(StartBuyTransactionCommand command) {
        notNull(command.getCurrencyPair());
        notNull(command.getItemPrice());
        notNull(command.getTradeAmount());
        notNull(command.getCoinId());
        notNull(command.getOrderBookIdentifier());
        notNull(command.getPortfolioIdentifier());
        notNull(command.getTransactionIdentifier());

        final Order order = toOrder(command);

        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(order);
        Commission commission = commissionPolicy.calculateBuyCommission(order);
        final BigMoney totalCommission = commission.getBigMoneyCommission();

        final Money totalMoney = command.getTradeAmount().convertedTo(command.getItemPrice().getCurrencyUnit(),
                command.getItemPrice().getAmount()).toMoney(RoundingMode.HALF_EVEN);

        Transaction transaction =
                new Transaction(
                        command.getTransactionIdentifier(),
                        command.getCoinId(),
                        TransactionType.BUY,
                        command.getOrderBookIdentifier(),
                        command.getPortfolioIdentifier(),
                        command.getTradeAmount(),
                        command.getItemPrice(),
                        totalMoney.toBigMoney(),
                        totalCommission);
        repository.add(transaction);
    }

    @SuppressWarnings("unused")
    @CommandHandler
    public void handleStartSellTransactionCommand(StartSellTransactionCommand command) {
        notNull(command.getCurrencyPair());
        notNull(command.getItemPrice());
        notNull(command.getTradeAmount());
        notNull(command.getCoinId());
        notNull(command.getOrderBookIdentifier());
        notNull(command.getPortfolioIdentifier());
        notNull(command.getTransactionIdentifier());

        final Order order = toOrder(command);

        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(order);
        Commission commission = commissionPolicy.calculateSellCommission(order);
        final BigMoney totalCommission = commission.getBigMoneyCommission();

        final Money totalMoney = command.getTradeAmount().convertedTo(command.getItemPrice().getCurrencyUnit(),
                command.getItemPrice().getAmount()).toMoney(RoundingMode.HALF_EVEN);

        Transaction transaction =
                new Transaction(
                        command.getTransactionIdentifier(),
                        command.getCoinId(),
                        TransactionType.SELL,
                        command.getOrderBookIdentifier(),
                        command.getPortfolioIdentifier(),
                        command.getTradeAmount(),
                        command.getItemPrice(),
                        totalMoney.toBigMoney(),
                        totalCommission);
        repository.add(transaction);
    }

    private Order toOrder(StartBuyTransactionCommand command) {
        final Order order = new Order(OrderType.BUY);
        fillOrder(command, order);
        return order;
    }

    private Order toOrder(StartSellTransactionCommand command) {
        final Order order = new Order(OrderType.SELL);
        fillOrder(command, order);
        return order;
    }

    private void fillOrder(AbstractStartTransactionCommand command, Order order) {
        order.setItemRemaining(command.getTradeAmount());
        order.setTradeAmount(command.getTradeAmount());
        order.setItemPrice(command.getItemPrice());
        order.setPortfolioId(command.getPortfolioIdentifier());
        order.setOrderBookId(command.getOrderBookIdentifier());
        order.setCurrencyPair(command.getCurrencyPair());
//        order.setCoinId(command.getCoinId());
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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
                        transaction.cancel();
                        return null;
                    }
                }
        );
    }

    @SuppressWarnings("unused")
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
                        transaction.execute(
                                command.getTradeAmount(),
                                command.getItemPrice(),
                                command.getExecutedMoney(),
                                command.getCommission());
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
