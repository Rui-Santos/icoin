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

package com.icoin.trading.tradeengine.query.transaction;

import com.icoin.trading.tradeengine.domain.events.transaction.AbstractTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.AbstractTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.AbstractTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionType;
import com.icoin.trading.tradeengine.query.orderbook.OrderBookEntry;
import com.icoin.trading.tradeengine.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.transaction.repositories.TransactionQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Jettro Coenradie
 */
@Component
public class TransactionEventListener {

    private OrderBookQueryRepository orderBookQueryRepository;
    private TransactionQueryRepository transactionQueryRepository;

    @EventHandler
    public void handleEvent(BuyTransactionStartedEvent event) {
        startTransaction(event, TransactionType.BUY);
    }

    @EventHandler
    public void handleEvent(SellTransactionStartedEvent event) {
        startTransaction(event, TransactionType.SELL);
    }

    @EventHandler
    public void handleEvent(BuyTransactionCancelledEvent event) {
        changeStateOfTransaction(event.getTransactionIdentifier().toString(), TransactionState.CANCELLED);
    }

    @EventHandler
    public void handleEvent(SellTransactionCancelledEvent event) {
        changeStateOfTransaction(event.getTransactionIdentifier().toString(), TransactionState.CANCELLED);
    }

    @EventHandler
    public void handleEvent(BuyTransactionConfirmedEvent event) {
        changeStateOfTransaction(event.getTransactionIdentifier().toString(), TransactionState.CONFIRMED);
    }

    @EventHandler
    public void handleEvent(SellTransactionConfirmedEvent event) {
        changeStateOfTransaction(event.getTransactionIdentifier().toString(), TransactionState.CONFIRMED);
    }

    @EventHandler
    public void handleEvent(BuyTransactionExecutedEvent event) {
        executeTransaction(event);
    }

    @EventHandler
    public void handleEvent(SellTransactionExecutedEvent event) {
        executeTransaction(event);
    }

    @EventHandler
    public void handleEvent(BuyTransactionPartiallyExecutedEvent event) {
        partiallyExecuteTransaction(event);
    }

    @EventHandler
    public void handleEvent(SellTransactionPartiallyExecutedEvent event) {
        partiallyExecuteTransaction(event);
    }

    private void partiallyExecuteTransaction(AbstractTransactionPartiallyExecutedEvent event) {
        TransactionEntry transactionEntry = transactionQueryRepository.findOne(event.getTransactionIdentifier()
                .toString());

        BigDecimal value = transactionEntry.getAmountOfExecutedItems().multiply(transactionEntry.getPricePerItem()) ;
        BigDecimal additionalValue = event.getAmountOfExecutedItems().multiply(event.getItemPrice());
        BigDecimal newPrice = (value.add(additionalValue)).divide(event.getTotalOfExecutedItems(),2, RoundingMode.HALF_EVEN);

        transactionEntry.setState(TransactionState.PARTIALLYEXECUTED);
        transactionEntry.setAmountOfExecutedItems(event.getTotalOfExecutedItems());
        transactionEntry.setPricePerItem(newPrice);
        transactionQueryRepository.save(transactionEntry);
    }

    private void executeTransaction(AbstractTransactionExecutedEvent event) {
        TransactionEntry transactionEntry = transactionQueryRepository.findOne(event.getTransactionIdentifier()
                .toString());

        BigDecimal value = transactionEntry.getAmountOfExecutedItems().multiply(transactionEntry.getPricePerItem());
        BigDecimal additionalValue = event.getAmountOfItems().multiply(event.getItemPrice());
        BigDecimal newPrice = (value.add(additionalValue) ).divide(transactionEntry.getAmountOfItems(),
                8, RoundingMode.HALF_EVEN);

        transactionEntry.setState(TransactionState.EXECUTED);
        transactionEntry.setAmountOfExecutedItems(transactionEntry.getAmountOfItems());
        transactionEntry.setPricePerItem(newPrice);
        transactionQueryRepository.save(transactionEntry);
    }

    private void changeStateOfTransaction(String primaryKey, TransactionState newState) {
        TransactionEntry transactionEntry = transactionQueryRepository.findOne(primaryKey);
        transactionEntry.setState(newState);
        transactionQueryRepository.save(transactionEntry);
    }

    private void startTransaction(AbstractTransactionStartedEvent event, TransactionType type) {
        OrderBookEntry orderBookEntry = orderBookQueryRepository.findOne(event.getOrderbookIdentifier().toString());

        TransactionEntry entry = new TransactionEntry();
        entry.setAmountOfExecutedItems(BigDecimal.ZERO);
        entry.setAmountOfItems(event.getTotalItems());
        entry.setPricePerItem(event.getPricePerItem());
        entry.setPrimaryKey(event.getTransactionIdentifier().toString());
        entry.setOrderbookIdentifier(event.getOrderbookIdentifier().toString());
        entry.setPortfolioIdentifier(event.getPortfolioIdentifier().toString());
        entry.setState(TransactionState.STARTED);
        entry.setType(type);
        entry.setCoinName(orderBookEntry.getCoinName());

        transactionQueryRepository.save(entry);
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderBookQueryRepository(OrderBookQueryRepository orderBookQueryRepository) {
        this.orderBookQueryRepository = orderBookQueryRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setTransactionQueryRepository(TransactionQueryRepository transactionQueryRepository) {
        this.transactionQueryRepository = transactionQueryRepository;
    }
}
