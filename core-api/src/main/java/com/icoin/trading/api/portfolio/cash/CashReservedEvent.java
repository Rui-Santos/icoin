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

package com.icoin.trading.api.portfolio.cash;

import com.icoin.trading.api.orders.trades.TransactionId;
import com.icoin.trading.api.orders.trades.PortfolioId;

/**
 * @author Jettro Coenradie
 */
public class CashReservedEvent {
    private PortfolioId portfolioIdentifier;
    private TransactionId transactionIdentifier;
    private long amountToReserve;

    public CashReservedEvent(PortfolioId portfolioIdentifier, TransactionId transactionIdentifier, long amountToReserve) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.transactionIdentifier = transactionIdentifier;
        this.amountToReserve = amountToReserve;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public long getAmountToReserve() {
        return amountToReserve;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }
}
