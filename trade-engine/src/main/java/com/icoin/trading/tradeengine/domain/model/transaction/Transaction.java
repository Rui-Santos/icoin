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
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
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
                       BigMoney totalCommission) {
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
                        totalCommission));
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
                        totalCommission));
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
                apply(new BuyTransactionCancelledEvent(transactionId, coinId));
                break;
            case SELL:
                apply(new SellTransactionCancelledEvent(transactionId, coinId));
                break;
        }
    }

    public void execute(BigMoney amountOfItem, BigMoney itemPrice, BigMoney executedMoney, BigMoney commission) {
        switch (this.type) {
            case BUY:
                if (isPartiallyExecuted(amountOfItem)) {
                    apply(new BuyTransactionPartiallyExecutedEvent(transactionId,
                            coinId,
                            amountOfItem,
                            amountOfItem.plus(executedAmount),
                            itemPrice,
                            executedMoney,
                            commission));
                } else {
                    apply(new BuyTransactionExecutedEvent(
                            transactionId,
                            coinId,
                            amountOfItem,
                            itemPrice,
                            executedMoney,
                            commission));
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
                            commission));
                } else {
                    apply(new SellTransactionExecutedEvent(
                            transactionId,
                            coinId,
                            amountOfItem,
                            itemPrice,
                            executedMoney,
                            commission));
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
