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

package com.icoin.trading.api.orders.trades;

/**
 * <p>A new Buy Order is placed.</p>
 *
 * @author Allard Buijze
 */
public class BuyOrderPlacedEvent extends AbstractOrderPlacedEvent {

    public BuyOrderPlacedEvent(OrderBookId orderBookId, OrderId orderId, TransactionId transactionId, long tradeCount,
                               long itemPrice, PortfolioId portfolioId) {
        super(orderBookId, orderId, transactionId, tradeCount, itemPrice, portfolioId);
    }
}
