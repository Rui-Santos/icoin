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

import com.homhon.base.domain.Identity;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionConfirmedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionPartiallyExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.tradeengine.domain.TransactionType;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class Transaction extends AxonAnnotatedAggregateRoot<Transaction, TransactionId> {
    private static final long serialVersionUID = 6321449194110602618L;

    @Identity
    @AggregateIdentifier
    private TransactionId transactionId;
    private BigMoney amountOfItem;
    private BigMoney executedAmount;
    private BigMoney totalMoney;
    private BigMoney totalCommission;
    private TransactionType type;
    private CoinId coinId;


    @SuppressWarnings("UnusedDeclaration")
    protected Transaction() {
    }

    public Transaction(TransactionId transactionId,
                       CoinId coinId,
                       TransactionType type,
                       OrderBookId orderBookIdentifier,
                       PortfolioId portfolioIdentifier,
                       BigMoney amountOfItems,
                       BigMoney pricePerItem,
                       BigMoney totalMoney,
                       BigMoney totalCommission,
                       Date time) {
        switch (type) {
            case BUY:
                apply(new BuyTransactionStartedEvent(
                        transactionId,
                        coinId,
                        orderBookIdentifier,
                        portfolioIdentifier,
                        amountOfItems,
                        pricePerItem,
                        totalMoney,
                        totalCommission,
                        time));
                break;
            case SELL:
                apply(new SellTransactionStartedEvent(
                        transactionId,
                        coinId,
                        orderBookIdentifier,
                        portfolioIdentifier,
                        amountOfItems,
                        pricePerItem,
                        totalMoney,
                        totalCommission,
                        time));
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

    public void cancel(Date time) {
        switch (this.type) {
            case BUY:
                apply(new BuyTransactionCancelledEvent(transactionId, coinId, time));
                break;
            case SELL:
                apply(new SellTransactionCancelledEvent(transactionId, coinId, time));
                break;
        }
    }

    public void execute(BigMoney amountOfItem, BigMoney itemPrice, BigMoney executedMoney, BigMoney commission, Date time) {
        switch (this.type) {
            case BUY:
                if (isPartiallyExecuted(amountOfItem)) {
                    apply(new BuyTransactionPartiallyExecutedEvent(transactionId,
                            coinId,
                            amountOfItem,
                            amountOfItem.plus(executedAmount),
                            itemPrice,
                            executedMoney,
                            commission,
                            time));
                } else {
                    apply(new BuyTransactionExecutedEvent(
                            transactionId,
                            coinId,
                            amountOfItem,
                            itemPrice,
                            executedMoney,
                            commission,
                            time));
                }
                break;
            case SELL:
                if (isPartiallyExecuted(amountOfItem)) {
                    apply(new SellTransactionPartiallyExecutedEvent(transactionId,
                            coinId,
                            amountOfItem,
                            amountOfItem.plus(executedAmount),
                            itemPrice,
                            executedMoney,
                            commission,
                            time));
                } else {
                    apply(new SellTransactionExecutedEvent(
                            transactionId,
                            coinId,
                            amountOfItem,
                            itemPrice,
                            executedMoney,
                            commission,
                            time));
                }
                break;
        }
    }

    private boolean isPartiallyExecuted(BigMoney amountOfItems) {
        return this.executedAmount.plus(amountOfItems).compareTo(this.amountOfItem) < 0;
    }

    @EventHandler
    public void onBuyTransactionStarted(BuyTransactionStartedEvent event) {
        this.type = TransactionType.BUY;
        this.transactionId = event.getTransactionIdentifier();
        this.coinId = event.getCoinId();
        this.amountOfItem = event.getTotalItem();
        this.executedAmount = BigMoney.zero(event.getTotalItem().getCurrencyUnit());
        this.totalMoney = event.getTotalMoney();
        this.totalCommission = event.getTotalCommission();
    }

    @EventHandler
    public void onSellTransactionStarted(SellTransactionStartedEvent event) {
        this.type = TransactionType.SELL;
        this.transactionId = event.getTransactionIdentifier();
        this.coinId = event.getCoinId();
        this.amountOfItem = event.getTotalItem();
        this.executedAmount = BigMoney.zero(event.getTotalItem().getCurrencyUnit());
        this.totalMoney = event.getTotalMoney();
        this.totalCommission = event.getTotalCommission();
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

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", amountOfItem=" + amountOfItem +
                ", executedAmount=" + executedAmount +
                ", totalCommission=" + totalCommission +
                ", type=" + type +
                ", coinId=" + coinId +
                '}';
    }
}
