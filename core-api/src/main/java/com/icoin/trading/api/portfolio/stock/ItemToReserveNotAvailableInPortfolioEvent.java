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

package com.icoin.trading.api.portfolio.stock;

import com.icoin.trading.api.orders.trades.TransactionId;
import com.icoin.trading.api.orders.trades.OrderBookId;
import com.icoin.trading.api.orders.trades.PortfolioId;

/**
 * @author Jettro Coenradie
 */
public class ItemToReserveNotAvailableInPortfolioEvent {
    private PortfolioId portfolioIdentifier;
    private OrderBookId orderBookIdentifier;
    private TransactionId transactionIdentifier;

    public ItemToReserveNotAvailableInPortfolioEvent(PortfolioId portfolioIdentifier,
                                                     OrderBookId orderBookIdentifier,
                                                     TransactionId transactionIdentifier) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.orderBookIdentifier = orderBookIdentifier;
        this.transactionIdentifier = transactionIdentifier;
    }

    public OrderBookId getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }
}
