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

import com.icoin.trading.tradeengine.domain.model.transaction.TransactionType;
import org.hamcrest.Description;
import org.joda.money.BigMoney;
import org.mockito.ArgumentMatcher;

/**
 * @author Jettro Coenradie
 */
public class TransactionEntryMatcher extends ArgumentMatcher<TransactionEntry> {

    private String problem;
    private TransactionState state;
    private TransactionType type;
    private String coinName;
    private BigMoney amountOfItem;
    private BigMoney amountOfItemsExecuted;
    private BigMoney pricePerItem;

    public TransactionEntryMatcher(BigMoney amountOfItem, BigMoney amountOfItemsExecuted, String coinName, BigMoney pricePerItem,
                                   TransactionState state, TransactionType type) {
        this.amountOfItem = amountOfItem;
        this.amountOfItemsExecuted = amountOfItemsExecuted;
        this.coinName = coinName;
        this.pricePerItem = pricePerItem;
        this.state = state;
        this.type = type;
    }

    @Override
    public boolean matches(Object argument) {
        if (!(argument instanceof TransactionEntry)) {
            problem = String.format("Wrong argument type, required %s but received %s",
                    TransactionEntry.class.getName(),
                    argument.getClass().getName());
            return false;
        }
        TransactionEntry transactionEntry = (TransactionEntry) argument;
        if (amountOfItem.minus(transactionEntry.getAmountOfItem()).isPositive()) {
            problem = String.format("Amount of items is not %s but %s",
                    amountOfItem,
                    transactionEntry.getAmountOfItem());
            return false;
        }

        if (amountOfItemsExecuted.minus(transactionEntry.getAmountOfExecutedItem()).isPositive()) {
            problem = String.format("Amount of executed items is not %s but %s",
                    amountOfItemsExecuted,
                    transactionEntry.getAmountOfExecutedItem());
            return false;
        }
        if (!coinName.equals(transactionEntry.getCoinName())) {
            problem = String.format("Coin name is not %s but %s", coinName, transactionEntry.getCoinName());
            return false;
        }

        if (pricePerItem.minus(transactionEntry.getPricePerItem()).isPositive()) {
            problem = String.format("Price per item is not %s but %s",
                    pricePerItem,
                    transactionEntry.getPricePerItem());
            return false;
        }
        if (state != transactionEntry.getState()) {
            problem = String.format("State is not %s but %s", state, transactionEntry.getState());
            return false;
        }
        if (type != transactionEntry.getType()) {
            problem = String.format("Type is not %s but %s", type, transactionEntry.getType());
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(problem);
    }
}
