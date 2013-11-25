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

import com.icoin.trading.api.orders.trades.TransactionId;
import com.icoin.trading.api.orders.transaction.CancelTransactionCommand;
import org.hamcrest.Description;

/**
 * @author Jettro Coenradie
 */
public class CancelTransactionCommandMatcher extends BaseCommandMatcher<CancelTransactionCommand> {

    private TransactionId transactionIdentifier;

    public CancelTransactionCommandMatcher(TransactionId transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    @Override
    protected boolean doMatches(CancelTransactionCommand command) {
        return command.getTransactionIdentifier().equals(transactionIdentifier);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("CancelTransactionCommand for Transaction with identifier [")
                .appendValue(transactionIdentifier)
                .appendText("]");
    }
}
