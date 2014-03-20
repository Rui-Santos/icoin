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

import com.icoin.trading.api.tradeengine.command.transaction.ExecutedTransactionCommand;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.hamcrest.Description;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class ExecutedTransactionCommandMatcher extends BaseCommandMatcher<ExecutedTransactionCommand> {

    private TransactionId transactionIdentifier;
    private CoinId coinId;
    private BigMoney amountOfItem;
    private BigMoney itemPrice;
    private BigMoney executedMoney;
    private BigMoney commission;

    public ExecutedTransactionCommandMatcher(BigMoney amountOfItem,
                                             BigMoney itemPrice,
                                             BigMoney executedMoney,
                                             BigMoney commission,
                                             TransactionId transactionIdentifier,
                                             CoinId coinId) {
        this.amountOfItem = amountOfItem;
        this.itemPrice = itemPrice;
        this.executedMoney = executedMoney;
        this.commission = commission;
        this.transactionIdentifier = transactionIdentifier;
        this.coinId = coinId;
    }

    @Override
    protected boolean doMatches(ExecutedTransactionCommand command) {
        return command.getTransactionIdentifier().equals(transactionIdentifier)
                && command.getCoinId().equals(coinId)
                && command.getTradeAmount().isEqual(amountOfItem)
                && command.getExecutedMoney().isEqual(executedMoney)
                && command.getCommission().isEqual(commission)
                && command.getItemPrice().isEqual(itemPrice);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ExecutedTransactionCommand with amountOfItem [")
                .appendValue(amountOfItem)
                .appendText("], itemPrice [")
                .appendValue(itemPrice)
                .appendText("], executedMoney [")
                .appendValue(executedMoney)
                .appendText("], commission [")
                .appendValue(commission)
                .appendText("] for Transaction with identifier [")
                .appendValue(transactionIdentifier)
                .appendText("]");
    }
}
