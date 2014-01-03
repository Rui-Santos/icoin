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
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedClearedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemToReserveNotAvailableInPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.NotEnoughItemAvailableToReserveInPortfolio;
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
//    private Map<CoinId, Item> reservedItems = new HashMap<CoinId, Item>();

    private BigMoney amountOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);
    private BigMoney reservedAmountOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);
//    private BigMoney reservedCommissionOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);

    protected Portfolio() {
    }

    public Portfolio(PortfolioId portfolioId, UserId userIdentifier) {
        apply(new PortfolioCreatedEvent(portfolioId, userIdentifier));
    }

    public void addItem(CoinId coinId, BigMoney amountOfItemToAdd) {
        apply(new ItemAddedToPortfolioEvent(portfolioId, coinId, amountOfItemToAdd));
    }

    public void reserveItem(CoinId coinId, TransactionId transactionIdentifier, BigMoney amountOfItemToReserve, BigMoney commission) {
        if (!availableCoins.containsKey(coinId)) {
            apply(new ItemToReserveNotAvailableInPortfolioEvent(portfolioId, coinId, transactionIdentifier));
        } else {
            BigMoney availableAmountOfItem = availableCoins.get(coinId).getAvailableAmount();
            final BigMoney totalReserved = amountOfItemToReserve.plus(commission);
            if (availableAmountOfItem.compareTo(totalReserved.toMoney(RoundingMode.HALF_EVEN)) < 0) {
                apply(new NotEnoughItemAvailableToReserveInPortfolio(
                        portfolioId, coinId, transactionIdentifier, availableAmountOfItem, totalReserved));
            } else {
                apply(new ItemReservedEvent(portfolioId, coinId, transactionIdentifier, totalReserved));
            }
        }
    }

    public void confirmReservation(CoinId coinId, TransactionId transactionIdentifier,
                                   BigMoney amount, BigMoney commission) {
        apply(new ItemReservationConfirmedForPortfolioEvent(
                portfolioId,
                coinId,
                transactionIdentifier,
                amount,
                commission));
    }

    public void cancelReservation(CoinId coinId, TransactionId transactionIdentifier, BigMoney leftTotalItem, BigMoney leftCommission) {
        apply(new ItemReservationCancelledForPortfolioEvent(
                portfolioId,
                coinId,
                transactionIdentifier,
                leftTotalItem,
                leftCommission));
    }

    public void addMoney(BigMoney money) {
        apply(new CashDepositedEvent(portfolioId, money));
    }

    public void makePayment(BigMoney amountToPayInCents) {
        apply(new CashWithdrawnEvent(portfolioId, amountToPayInCents));
    }

    public void reserveMoney(TransactionId transactionIdentifier, BigMoney totalMoney, BigMoney totalCommission) {
        BigMoney amountToReserve = totalMoney.plus(totalCommission);

        if (amountOfMoney.compareTo(amountToReserve) >= 0) {
            apply(new CashReservedEvent(portfolioId, transactionIdentifier, totalMoney, totalCommission));
        } else {
            apply(new CashReservationRejectedEvent(portfolioId, transactionIdentifier, totalMoney, totalCommission));
        }
    }

    public void clearReservedMoney(TransactionId transactionIdentifier, BigMoney moneyToClear) {
        apply(new CashReservedClearedEvent(portfolioId, transactionIdentifier, moneyToClear));
    }

    public void cancelMoneyReservation(TransactionId transactionIdentifier, BigMoney leftTotalMoney, BigMoney leftCommission) {
        apply(new CashReservationCancelledEvent(portfolioId, transactionIdentifier, leftTotalMoney, leftCommission));
    }

    public void confirmMoneyReservation(TransactionId transactionIdentifier, BigMoney amountOfMoney, BigMoney commission) {
        apply(new CashReservationConfirmedEvent(portfolioId, transactionIdentifier, amountOfMoney, commission));
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
    public void onItemAddedToPortfolio(ItemAddedToPortfolioEvent event) {
        CurrencyUnit currencyUnit = currencyUnit(event.getAmountOfItemAdded());
        Item available = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
        availableCoins.put(event.getCoinId(), available.add(event.getAmountOfItemAdded()));
    }

    private Item createItem(CoinId coinId, CurrencyUnit currencyUnit) {
        final Item item = new Item();
        item.setCoinId(coinId);
        item.setTotalAmount(BigMoney.zero(currencyUnit));
        item.setReservedAmount(BigMoney.zero(currencyUnit));

        return item;
    }

    @EventHandler
    public void onItemReserved(ItemReservedEvent event) {
        CurrencyUnit currencyUnit = currencyUnit(event.getAmountOfItemReserved());
        Item available = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
        availableCoins.put(event.getCoinId(), available.reserve(event.getAmountOfItemReserved()));
//        Item reserved = obtainCurrentReservedItem(event.getCoinId(), currencyUnit);
//        reservedItems.put(event.getCoinId(), reserved.add(event.getAmountOfItemReserved()));
    }

    @EventHandler
    public void onReservationConfirmed(ItemReservationConfirmedForPortfolioEvent event) {
        final CurrencyUnit currencyUnit = currencyUnit(event.getAmount());
        Item reserved = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
        final BigMoney amount = event.getAmount().plus(event.getCommission());
        availableCoins.put(event.getCoinId(), reserved.confirmReserved(amount));


        //available this should be wrong, comment it out
//        Item available = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
//        availableCoins.put(event.getCoinId(), available.subtract(event.getAmount()));
    }

    @EventHandler
    public void onReservationCancelled(ItemReservationCancelledForPortfolioEvent event) {
        final CurrencyUnit currencyUnit = currencyUnit(event.getLeftTotalItem());
//        Item reserved = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
//        reservedItems.put(event.getCoinId(), reserved.subtract(event.getLeftTotalItem()));

        Item available = obtainCurrentAvailableItem(event.getCoinId(), currencyUnit);
        availableCoins.put(event.getCoinId(), available.cancelReserved(event.getLeftTotalItem()));
    }

    @EventHandler
    public void onCashReservedClearedEvent(CashReservedClearedEvent event) {
        reservedAmountOfMoney = reservedAmountOfMoney.minus(event.getAmountToClear());
        amountOfMoney = amountOfMoney.plus(event.getAmountToClear());
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
        final BigMoney reservedAmount = event.getTotalMoney().plus(event.getTotalCommission());
        amountOfMoney = amountOfMoney.minus(reservedAmount);
        reservedAmountOfMoney = reservedAmountOfMoney.plus(reservedAmount);
    }

    @EventHandler
    public void onMoneyReservationCancelled(CashReservationCancelledEvent event) {
        final BigMoney total = event.getLeftTotalMoney().plus(event.getLeftCommission());
        reservedAmountOfMoney = reservedAmountOfMoney.minus(total);
        amountOfMoney = amountOfMoney.plus(total);
    }

    @EventHandler
    public void onMoneyReservationConfirmed(CashReservationConfirmedEvent event) {
        final BigMoney totalReserved = event.getAmountOfMoney().plus(event.getCommission());
        reservedAmountOfMoney = reservedAmountOfMoney.minus(totalReserved);
    }

    /* UTILITY METHODS */
    private Item obtainCurrentAvailableItem(CoinId coinId, CurrencyUnit currencyUnit) {
        if (availableCoins.containsKey(coinId)) {
            return availableCoins.get(coinId);
        }
        return createItem(coinId, currencyUnit);
    }

//    private Item obtainCurrentReservedItem(CoinId coinId, CurrencyUnit currencyUnit) {
//        if (reservedItems.containsKey(coinId)) {
//            return reservedItems.get(coinId);
//        }
//
//        return createItem(coinId, currencyUnit);
//    }

    @Override
    public PortfolioId getIdentifier() {
        return portfolioId;
    }
}