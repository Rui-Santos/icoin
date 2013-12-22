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

package com.icoin.trading.tradeengine.domain.model.transaction;

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
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class Transaction extends AbstractAnnotatedAggregateRoot {
    private static final long serialVersionUID = 1299083385130634014L;

    @AggregateIdentifier
    private TransactionId transactionId;
    private BigMoney amountOfItem;
    private BigMoney executedAmount;
    private TransactionType type;


    @SuppressWarnings("UnusedDeclaration")
    protected Transaction() {
    }

    public Transaction(TransactionId transactionId,
                       TransactionType type,
                       OrderBookId orderbookIdentifier,
                       PortfolioId portfolioIdentifier,
                       BigMoney amountOfItems,
                       BigMoney pricePerItem) {
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

    public void confirm(Date confirmDate) {
        switch (this.type) {
            case BUY:
                apply(new BuyTransactionConfirmedEvent(transactionId, confirmDate));
                break;
            case SELL:
                apply(new SellTransactionConfirmedEvent(transactionId, confirmDate));
                break;
        }
    }

    public void cancel(BigMoney cancelledPrice) {
        switch (this.type) {
            case BUY:
                apply(new BuyTransactionCancelledEvent(transactionId, amountOfItem, executedAmount, cancelledPrice));
                break;
            case SELL:
                apply(new SellTransactionCancelledEvent(transactionId, amountOfItem, executedAmount, cancelledPrice));
                break;
        }
    }

    public void execute(BigMoney amountOfItems, BigMoney itemPrice) {
        switch (this.type) {
            case BUY:
                if (isPartiallyExecuted(amountOfItems)) {
                    apply(new BuyTransactionPartiallyExecutedEvent(transactionId,
                            amountOfItems,
                            amountOfItems.plus(executedAmount),
                            itemPrice));
                } else {
                    apply(new BuyTransactionExecutedEvent(transactionId, amountOfItems, itemPrice));
                }
                break;
            case SELL:
                if (isPartiallyExecuted(amountOfItems)) {
                    apply(new SellTransactionPartiallyExecutedEvent(transactionId,
                            amountOfItems,
                            amountOfItems.plus(executedAmount),
                            itemPrice));
                } else {
                    apply(new SellTransactionExecutedEvent(transactionId, amountOfItems, itemPrice));
                }
                break;
        }
    }

    private boolean isPartiallyExecuted(BigMoney amountOfItems) {
        return this.executedAmount.plus(amountOfItems).compareTo(this.amountOfItem) < 0;
    }

    @EventHandler
    public void onBuyTransactionStarted(BuyTransactionStartedEvent event) {
        this.transactionId = event.getTransactionIdentifier();
        this.amountOfItem = event.getTotalItem();
        this.executedAmount = BigMoney.zero(event.getTotalItem().getCurrencyUnit());
        this.type = TransactionType.BUY;
    }

    @EventHandler
    public void onSellTransactionStarted(SellTransactionStartedEvent event) {
        this.transactionId = event.getTransactionIdentifier();
        this.amountOfItem = event.getTotalItem();
        this.executedAmount = BigMoney.zero(event.getTotalItem().getCurrencyUnit());
        this.type = TransactionType.SELL;
    }

    @EventHandler
    public void onTransactionExecuted(BuyTransactionExecutedEvent event) {
        this.executedAmount = this.amountOfItem;
    }

    @EventHandler
    public void onTransactionExecuted(SellTransactionExecutedEvent event) {
        this.executedAmount = this.amountOfItem;
    }

    @EventHandler
    public void onTransactionPartiallyExecuted(SellTransactionPartiallyExecutedEvent event) {
        this.executedAmount = executedAmount.plus(event.getAmountOfExecutedItem());
    }

    @EventHandler
    public void onTransactionPartiallyExecuted(BuyTransactionPartiallyExecutedEvent event) {
        this.executedAmount = executedAmount.plus(event.getAmountOfExecutedItem());
    }

    @Override
    public TransactionId getIdentifier() {
        return transactionId;
    }
}
