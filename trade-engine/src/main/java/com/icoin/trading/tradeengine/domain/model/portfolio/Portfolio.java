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

import com.icoin.trading.tradeengine.Constants;
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
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.users.domain.UserId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.math.RoundingMode;
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
    private UserId userIdentifier;
    private Map<CoinId, Item> availableCoins = new HashMap<CoinId, Item>();
    private Map<CoinId, Item> reservedItems = new HashMap<CoinId, Item>();

    private BigMoney amountOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);
    private BigMoney reservedAmountOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);
    private BigMoney reservedCommissionOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);

    protected Portfolio() {
    }

    public Portfolio(PortfolioId portfolioId, UserId userIdentifier) {
        apply(new PortfolioCreatedEvent(portfolioId, userIdentifier));
    }

    public void addItems(CoinId coinId, BigMoney amountOfItemToAdd) {
        apply(new ItemsAddedToPortfolioEvent(portfolioId, coinId, amountOfItemToAdd));
    }

    public void reserveItems(CoinId coinId, TransactionId transactionIdentifier, BigMoney amountOfItemsToReserve) {
        if (!availableCoins.containsKey(coinId)) {
            apply(new ItemToReserveNotAvailableInPortfolioEvent(portfolioId, coinId, transactionIdentifier));
        } else {
            BigMoney availableAmountOfItems = availableCoins.get(coinId).getAvailableAmount();
            if (availableAmountOfItems.compareTo(amountOfItemsToReserve.toMoney(RoundingMode.HALF_EVEN)) < 0) {
                apply(new NotEnoughItemsAvailableToReserveInPortfolio(
                        portfolioId, coinId, transactionIdentifier, availableAmountOfItems, amountOfItemsToReserve));
            } else {
                apply(new ItemsReservedEvent(portfolioId, coinId, transactionIdentifier, amountOfItemsToReserve));
            }
        }
    }

    public void confirmReservation(CoinId coinId, TransactionId transactionIdentifier,
                                   BigMoney amountOfItemsToConfirm) {
        apply(new ItemReservationConfirmedForPortfolioEvent(
                portfolioId,
                coinId,
                transactionIdentifier,
                amountOfItemsToConfirm));
    }

    public void cancelReservation(CoinId coinId, TransactionId transactionIdentifier, BigMoney amountOfItemsToCancel) {
        apply(new ItemReservationCancelledForPortfolioEvent(
                portfolioId,
                coinId,
                transactionIdentifier,
                amountOfItemsToCancel));
    }

    public void addMoney(BigMoney money) {
        apply(new CashDepositedEvent(portfolioId, money));
    }

    public void makePayment(BigMoney amountToPayInCents) {
        apply(new CashWithdrawnEvent(portfolioId, amountToPayInCents));
    }

    public void reserveMoney(TransactionId transactionIdentifier, BigMoney amountToReserve) {
        if (amountOfMoney.compareTo(amountToReserve) >= 0) {
            apply(new CashReservedEvent(portfolioId, transactionIdentifier, amountToReserve));
        } else {
            apply(new CashReservationRejectedEvent(portfolioId, transactionIdentifier, amountToReserve));
        }
    }

    public void cancelMoneyReservation(TransactionId transactionIdentifier, BigMoney amountOfMoneyToCancel) {
        apply(new CashReservationCancelledEvent(portfolioId, transactionIdentifier, amountOfMoneyToCancel));
    }

    public void confirmMoneyReservation(TransactionId transactionIdentifier, BigMoney amountOfMoneyToConfirm) {
        apply(new CashReservationConfirmedEvent(portfolioId, transactionIdentifier, amountOfMoneyToConfirm));
    }

    private static CurrencyUnit currencyUnit(BigMoney money) {
        return money.getCurrencyUnit();
    }

    /* EVENT HANDLING */
    @EventHandler
    public void onPortfolioCreated(PortfolioCreatedEvent event) {
        this.portfolioId = event.getPortfolioId();
        this.userIdentifier = event.getUserId();
    }

    @EventHandler
    public void onItemsAddedToPortfolio(ItemsAddedToPortfolioEvent event) {
        CurrencyUnit currencyUnit = currencyUnit(event.getAmountOfItemAdded());
        Item available = obtainCurrentAvailableItem(event.getCoinId());

        if(available == null){
           createItem(event.getCoinId(), currencyUnit);
        }

        availableCoins.put(event.getCoinId(), available.plus(event.getAmountOfItemAdded()));
    }

    private Item createItem(CoinId coinId, CurrencyUnit currencyUnit) {
        final Item item = new Item();
        item.setCoinId(coinId);
        item.setTotalAmount(BigMoney.zero(currencyUnit));
        item.setReservedAmount(BigMoney.zero(currencyUnit));

        return item;
    }

    @EventHandler
    public void onItemsReserved(ItemsReservedEvent event) {
        CurrencyUnit currencyUnit = currencyUnit(event.getAmountOfItemReserved());
        BigMoney available = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
        availableCoins.put(event.getCoinId(), available.minus(event.getAmountOfItemReserved()));

        BigMoney reserved = obtainCurrentReservedItem(event.getCoinId(), currencyUnit);
        reservedItems.put(event.getCoinId(), reserved.plus(event.getAmountOfItemReserved()));
    }

    @EventHandler
    public void onReservationConfirmed(ItemReservationConfirmedForPortfolioEvent event) {
        final CurrencyUnit currencyUnit = currencyUnit(event.getAmountOfConfirmedItem());
        BigMoney reserved = obtainCurrentReservedItem(event.getCoinId(), currencyUnit);
        reservedItems.put(event.getCoinId(), reserved.minus(event.getAmountOfConfirmedItem()));

        BigMoney available = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
        availableCoins.put(event.getCoinId(), available.minus(event.getAmountOfConfirmedItem()));
    }

    @EventHandler
    public void onReservationCancelled(ItemReservationCancelledForPortfolioEvent event) {
        final CurrencyUnit currencyUnit = currencyUnit(event.getAmountOfCancelledAmount());
        BigMoney reserved = obtainCurrentReservedItem(event.getCoinId(), currencyUnit);
        reservedItems.put(event.getCoinId(), reserved.plus(event.getAmountOfCancelledAmount()));

        BigMoney available = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
        availableCoins.put(event.getCoinId(), available.plus(event.getAmountOfCancelledAmount()));
    }

    @EventHandler
    public void onMoneyAddedToPortfolio(CashDepositedEvent event) {
        amountOfMoney = amountOfMoney.plus(event.getMoneyAdded());
    }

    @EventHandler
    public void onPaymentMadeFromPortfolio(CashWithdrawnEvent event) {
        amountOfMoney = amountOfMoney.minus(event.getAmountPaid());
    }

    @EventHandler
    public void onMoneyReservedFromPortfolio(CashReservedEvent event) {
        //double check if amount of money is not enough 
        if (amountOfMoney.compareTo(event.getAmountToReserve()) < 0) {
            apply(new CashReservationRejectedEvent(portfolioId, event.getTransactionIdentifier(), event.getAmountToReserve()));
        }
        amountOfMoney = amountOfMoney.minus(event.getAmountToReserve());
        reservedAmountOfMoney = amountOfMoney.plus(event.getAmountToReserve());
    }

    @EventHandler
    public void onMoneyReservationCancelled(CashReservationCancelledEvent event) {
        amountOfMoney = amountOfMoney.plus(event.getAmountOfMoneyToCancel());
        reservedAmountOfMoney = reservedAmountOfMoney.minus(event.getAmountOfMoneyToCancel());
    }

    @EventHandler
    public void onMoneyReservationConfirmed(CashReservationConfirmedEvent event) {
        reservedAmountOfMoney = reservedAmountOfMoney.minus(event.getAmountOfConfirmedMoney());
    }

    /* UTILITY METHODS */
    private Item obtainCurrentAvailableItem(CoinId coinId) {
        Item available = null;
        if (availableCoins.containsKey(coinId)) {
            available = availableCoins.get(coinId);
        }
        return available;
    }

    private BigMoney obtainCurrentReservedItem(CoinId coinId, CurrencyUnit currencyUnit) {
        BigMoney reserved = BigMoney.zero(currencyUnit);
        if (reservedItems.containsKey(coinId)) {
            reserved = reservedItems.get(coinId);
        }
        return reserved;
    }

    @Override
    public PortfolioId getIdentifier() {
        return portfolioId;
    }
}