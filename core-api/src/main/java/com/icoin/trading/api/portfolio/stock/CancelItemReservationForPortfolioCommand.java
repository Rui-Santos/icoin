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
 * Cancel a reservation for an amount of items for the OrderBook belonging to the provided identifier in the Portfolio
 * of the provided identifier.
 *
 * @author Jettro Coenradie
 */
public class CancelItemReservationForPortfolioCommand {

    private PortfolioId portfolioIdentifier;
    private OrderBookId orderBookIdentifier;
    private TransactionId transactionIdentifier;
    private long amountOfCancelledItems;

    public CancelItemReservationForPortfolioCommand(PortfolioId portfolioIdentifier,
                                                    OrderBookId orderBookIdentifier,
                                                    TransactionId transactionIdentifier,
                                                    long amountOfCancelledItems) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.orderBookIdentifier = orderBookIdentifier;
        this.transactionIdentifier = transactionIdentifier;

        this.amountOfCancelledItems = amountOfCancelledItems;
    }

    public long getAmountOfItemsToCancel() {
        return amountOfCancelledItems;
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
