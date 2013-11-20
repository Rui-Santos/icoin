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

package com.icoin.trading.orders.command;

import com.icoin.trading.api.orders.trades.OrderBookId;
import com.icoin.trading.api.orders.trades.TransactionId;
import com.icoin.trading.api.orders.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.api.orders.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.api.orders.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.api.orders.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.api.orders.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.api.orders.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.api.orders.transaction.SellTransactionConfirmedEvent;
import com.icoin.trading.api.orders.transaction.SellTransactionExecutedEvent;
import com.icoin.trading.api.orders.transaction.SellTransactionPartiallyExecutedEvent;
import com.icoin.trading.api.orders.transaction.SellTransactionStartedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import com.icoin.trading.api.orders.trades.PortfolioId;
import com.icoin.trading.api.orders.transaction.TransactionType;

/**
 * @author Jettro Coenradie
 */
public class Transaction extends AbstractAnnotatedAggregateRoot {
    private static final long serialVersionUID = 1299083385130634014L;

    @AggregateIdentifier
    private TransactionId transactionId;
    private long amountOfItems;
    private long amountOfExecutedItems;
    private TransactionType type;


    @SuppressWarnings("UnusedDeclaration")
    protected Transaction() {
    }

    public Transaction(TransactionId transactionId,
                       TransactionType type,
                       OrderBookId orderbookIdentifier,
                       PortfolioId portfolioIdentifier,
                       long amountOfItems,
                       long pricePerItem) {
        switch (type) {
            case BUY:
                apply(new BuyTransactionStartedEvent(transactionId,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        amountOfItems,
                        pricePerItem));
                break;
            case SELL:
                apply(new SellTransactionStartedEvent(transactionId,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        amountOfItems,
                        pricePerItem));
                break;
        }
    }

    public void confirm() {
        switch (this.type) {
            case BUY:
                apply(new BuyTransactionConfirmedEvent(transactionId));
                break;
            case SELL:
                apply(new SellTransactionConfirmedEvent(transactionId));
                break;
        }
    }

    public void cancel() {
        switch (this.type) {
            case BUY:
                apply(new BuyTransactionCancelledEvent(transactionId, amountOfItems, amountOfExecutedItems));
                break;
            case SELL:
                apply(new SellTransactionCancelledEvent(transactionId, amountOfItems, amountOfExecutedItems));
                break;
        }
    }

    public void execute(long amountOfItems, long itemPrice) {
        switch (this.type) {
            case BUY:
                if (isPartiallyExecuted(amountOfItems)) {
                    apply(new BuyTransactionPartiallyExecutedEvent(transactionId,
                            amountOfItems,
                            amountOfItems + amountOfExecutedItems,
                            itemPrice));
                } else {
                    apply(new BuyTransactionExecutedEvent(transactionId, amountOfItems, itemPrice));
                }
                break;
            case SELL:
                if (isPartiallyExecuted(amountOfItems)) {
                    apply(new SellTransactionPartiallyExecutedEvent(transactionId,
                            amountOfItems,
                            amountOfItems + amountOfExecutedItems,
                            itemPrice));
                } else {
                    apply(new SellTransactionExecutedEvent(transactionId, amountOfItems, itemPrice));
                }
                break;
        }
    }

    private boolean isPartiallyExecuted(long amountOfItems) {
        return this.amountOfExecutedItems + amountOfItems < this.amountOfItems;
    }

    @EventHandler
    public void onBuyTransactionStarted(BuyTransactionStartedEvent event) {
        this.transactionId = event.getTransactionIdentifier();
        this.amountOfItems = event.getTotalItems();
        this.type = TransactionType.BUY;
    }

    @EventHandler
    public void onSellTransactionStarted(SellTransactionStartedEvent event) {
        this.transactionId = event.getTransactionIdentifier();
        this.amountOfItems = event.getTotalItems();
        this.type = TransactionType.SELL;
    }

    @EventHandler
    public void onTransactionExecuted(BuyTransactionExecutedEvent event) {
        this.amountOfExecutedItems = this.amountOfItems;
    }

    @EventHandler
    public void onTransactionExecuted(SellTransactionExecutedEvent event) {
        this.amountOfExecutedItems = this.amountOfItems;
    }

    @EventHandler
    public void onTransactionPartiallyExecuted(SellTransactionPartiallyExecutedEvent event) {
        this.amountOfExecutedItems += event.getAmountOfExecutedItems();
    }

    @EventHandler
    public void onTransactionPartiallyExecuted(BuyTransactionPartiallyExecutedEvent event) {
        this.amountOfExecutedItems += event.getAmountOfExecutedItems();
    }

    @Override
    public TransactionId getIdentifier() {
        return transactionId;
    }
}
