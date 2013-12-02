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

package com.icoin.trading.tradeengine.domain.model.portfolio;

import com.icoin.trading.tradeengine.domain.events.portfolio.PortfolioCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationRejectedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemToReserveNotAvailableInPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.NotEnoughItemsAvailableToReserveInPortfolio;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.users.domain.UserId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Not a lot of checks are available. We will check if you still have item before you reserve them. Other than that
 * we will not do checks. It is possible to give more items than you reserve.
 * <p/>
 * When buying items you need to reserve cash. Reservations need to be confirmed or cancelled. It is up to the user
 * to confirm and cancel the right amounts. The Portfolio does not keep track of it.
 *
 * @author Jettro Coenradie
 */
public class Portfolio extends AbstractAnnotatedAggregateRoot {
    private static final long serialVersionUID = 996371335141649977L;

    @AggregateIdentifier
    private PortfolioId portfolioId;
    private Map<OrderBookId, BigDecimal> availableCoins = new HashMap<OrderBookId, BigDecimal>();
    private Map<OrderBookId, BigDecimal> reservedItems = new HashMap<OrderBookId, BigDecimal>();

    private BigDecimal amountOfMoney = BigDecimal.ZERO;
    private BigDecimal reservedAmountOfMoney = BigDecimal.ZERO;

    protected Portfolio() {
    }

    public Portfolio(PortfolioId portfolioId, UserId userIdentifier) {
        apply(new PortfolioCreatedEvent(portfolioId, userIdentifier));
    }

    public void addItems(OrderBookId orderBookIdentifier, BigDecimal amountOfItemsToAdd) {
        apply(new ItemsAddedToPortfolioEvent(portfolioId, orderBookIdentifier, amountOfItemsToAdd));
    }

    public void reserveItems(OrderBookId orderBookIdentifier, TransactionId transactionIdentifier, BigDecimal amountOfItemsToReserve) {
        if (!availableCoins.containsKey(orderBookIdentifier)) {
            apply(new ItemToReserveNotAvailableInPortfolioEvent(portfolioId, orderBookIdentifier, transactionIdentifier));
        } else {
            BigDecimal availableAmountOfItems = availableCoins.get(orderBookIdentifier);
            if (availableAmountOfItems.compareTo(amountOfItemsToReserve) < 0) {
                apply(new NotEnoughItemsAvailableToReserveInPortfolio(
                        portfolioId, orderBookIdentifier, transactionIdentifier, availableAmountOfItems, amountOfItemsToReserve));
            } else {
                apply(new ItemsReservedEvent(portfolioId, orderBookIdentifier, transactionIdentifier, amountOfItemsToReserve));
            }
        }
    }

    public void confirmReservation(OrderBookId orderBookIdentifier, TransactionId transactionIdentifier,
                                   BigDecimal amountOfItemsToConfirm) {
        apply(new ItemReservationConfirmedForPortfolioEvent(
                portfolioId,
                orderBookIdentifier,
                transactionIdentifier,
                amountOfItemsToConfirm));
    }

    public void cancelReservation(OrderBookId orderBookIdentifier, TransactionId transactionIdentifier, BigDecimal amountOfItemsToCancel) {
        apply(new ItemReservationCancelledForPortfolioEvent(
                portfolioId,
                orderBookIdentifier,
                transactionIdentifier,
                amountOfItemsToCancel));
    }

    public void addMoney(BigDecimal money) {
        apply(new CashDepositedEvent(portfolioId, money));
    }

    public void makePayment(BigDecimal amountToPayInCents) {
        apply(new CashWithdrawnEvent(portfolioId, amountToPayInCents));
    }

    public void reserveMoney(TransactionId transactionIdentifier, BigDecimal amountToReserve) {
        if (amountOfMoney.compareTo(amountToReserve) >= 0) {
            apply(new CashReservedEvent(portfolioId, transactionIdentifier, amountToReserve));
        } else {
            apply(new CashReservationRejectedEvent(portfolioId, transactionIdentifier, amountToReserve));
        }
    }

    public void cancelMoneyReservation(TransactionId transactionIdentifier, BigDecimal amountOfMoneyToCancel) {
        apply(new CashReservationCancelledEvent(portfolioId, transactionIdentifier, amountOfMoneyToCancel));
    }

    public void confirmMoneyReservation(TransactionId transactionIdentifier, BigDecimal amountOfMoneyToConfirm) {
        apply(new CashReservationConfirmedEvent(portfolioId, transactionIdentifier, amountOfMoneyToConfirm));
    }

    /* EVENT HANDLING */
    @EventHandler
    public void onPortfolioCreated(PortfolioCreatedEvent event) {
        this.portfolioId = event.getPortfolioId();
    }

    @EventHandler
    public void onItemsAddedToPortfolio(ItemsAddedToPortfolioEvent event) {
        BigDecimal available = obtainCurrentAvailableItems(event.getOrderBookIdentifier());
        availableCoins.put(event.getOrderBookIdentifier(), available.add(event.getAmountOfItemsAdded()));
    }

    @EventHandler
    public void onItemsReserved(ItemsReservedEvent event) {
        BigDecimal available = obtainCurrentAvailableItems(event.getOrderBookIdentifier());
        availableCoins.put(event.getOrderBookIdentifier(), available.subtract(event.getAmountOfItemsReserved()));

        BigDecimal reserved = obtainCurrentReservedItems(event.getOrderBookIdentifier());
        reservedItems.put(event.getOrderBookIdentifier(), reserved.add(event.getAmountOfItemsReserved()));
    }

    @EventHandler
    public void onReservationConfirmed(ItemReservationConfirmedForPortfolioEvent event) {
        BigDecimal reserved = obtainCurrentReservedItems(event.getOrderBookIdentifier());
        reservedItems.put(event.getOrderBookIdentifier(), reserved.subtract(event.getAmountOfConfirmedItems()));

        BigDecimal available = obtainCurrentAvailableItems(event.getOrderBookIdentifier());
        availableCoins.put(event.getOrderBookIdentifier(), available.subtract(event.getAmountOfConfirmedItems()));
    }

    @EventHandler
    public void onReservationCancelled(ItemReservationCancelledForPortfolioEvent event) {
        BigDecimal reserved = obtainCurrentReservedItems(event.getOrderBookIdentifier());
        reservedItems.put(event.getOrderBookIdentifier(), reserved.add(event.getAmountOfCancelledAmount()));

        BigDecimal available = obtainCurrentAvailableItems(event.getOrderBookIdentifier());
        availableCoins.put(event.getOrderBookIdentifier(), available.add(event.getAmountOfCancelledAmount()));
    }

    @EventHandler
    public void onMoneyAddedToPortfolio(CashDepositedEvent event) {
        amountOfMoney = amountOfMoney.add(event.getMoneyAdded());
    }

    @EventHandler
    public void onPaymentMadeFromPortfolio(CashWithdrawnEvent event) {
        amountOfMoney = amountOfMoney.subtract(event.getAmountPaid());
    }

    @EventHandler
    public void onMoneyReservedFromPortfolio(CashReservedEvent event) {
        amountOfMoney = amountOfMoney.subtract(event.getAmountToReserve());
        reservedAmountOfMoney = amountOfMoney.add(event.getAmountToReserve());
    }

    @EventHandler
    public void onMoneyReservationCancelled(CashReservationCancelledEvent event) {
        amountOfMoney = amountOfMoney.add(event.getAmountOfMoneyToCancel());
        reservedAmountOfMoney = reservedAmountOfMoney.subtract(event.getAmountOfMoneyToCancel());
    }

    @EventHandler
    public void onMoneyReservationConfirmed(CashReservationConfirmedEvent event) {
        reservedAmountOfMoney = reservedAmountOfMoney.subtract(event.getAmountOfMoneyConfirmedInCents());
    }

    /* UTILITY METHODS */
    private BigDecimal obtainCurrentAvailableItems(OrderBookId orderBookIdentifier) {
        BigDecimal available = BigDecimal.ZERO;
        if (availableCoins.containsKey(orderBookIdentifier)) {
            available = availableCoins.get(orderBookIdentifier);
        }
        return available;
    }

    private BigDecimal obtainCurrentReservedItems(OrderBookId orderBookIdentifier) {
        BigDecimal reserved = BigDecimal.ZERO;
        if (reservedItems.containsKey(orderBookIdentifier)) {
            reserved = reservedItems.get(orderBookIdentifier);
        }
        return reserved;
    }

    @Override
    public PortfolioId getIdentifier() {
        return portfolioId;
    }
}
