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

package com.icoin.trading.coin.domain;

import com.icoin.trading.coin.event.CoinCreatedEvent;
import com.icoin.trading.coin.event.OrderBookAddedToCoinEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import com.icoin.trading.api.orders.trades.OrderBookId;

/**
 * @author Jettro Coenradie
 */
public class Coin extends AbstractAnnotatedAggregateRoot {
    private static final long serialVersionUID = 8723320580782813954L;

    @AggregateIdentifier
    private CoinId coinId;

    @SuppressWarnings("UnusedDeclaration")
    protected Coin() {
    }

    public Coin(CoinId coinId, String name, long value, long amountOfShares) {
        apply(new CoinCreatedEvent(coinId, name, value, amountOfShares));
    }

    public void addOrderBook(OrderBookId orderBookId) {
        apply(new OrderBookAddedToCoinEvent(coinId, orderBookId));
    }

    @Override
    public CoinId getIdentifier() {
        return this.coinId;
    }

    @EventHandler
    public void handle(CoinCreatedEvent event) {
        this.coinId = event.getCoinIdentifier();
    }
}
