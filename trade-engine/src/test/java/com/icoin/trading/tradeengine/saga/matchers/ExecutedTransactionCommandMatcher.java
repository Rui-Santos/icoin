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

package com.icoin.trading.tradeengine.saga.matchers;

import com.icoin.trading.tradeengine.application.command.transaction.command.ExecutedTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.hamcrest.Description;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class ExecutedTransactionCommandMatcher extends BaseCommandMatcher<ExecutedTransactionCommand> {

    private TransactionId transactionIdentifier;
    private BigDecimal amountOfItems;
    private BigDecimal itemPrice;

    public ExecutedTransactionCommandMatcher(BigDecimal amountOfItems, BigDecimal itemPrice, TransactionId transactionIdentifier) {
        this.amountOfItems = amountOfItems;
        this.itemPrice = itemPrice;
        this.transactionIdentifier = transactionIdentifier;
    }

    @Override
    protected boolean doMatches(ExecutedTransactionCommand command) {
        return command.getTransactionIdentifier().equals(transactionIdentifier)
                && command.getAmountOfItems().subtract(amountOfItems).abs().doubleValue() < 0.000000000001
                && command.getItemPrice().subtract(itemPrice).abs().doubleValue() < 0.000000000001;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ExecutedTransactionCommand with amountOfItems [")
                .appendValue(amountOfItems)
                .appendText("], itemPrice [")
                .appendValue(itemPrice)
                .appendText("] for Transaction with identifier [")
                .appendValue(transactionIdentifier)
                .appendText("]");
    }
}
