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

package com.icoin.trading.tradeengine.domain.model.coin;

import com.icoin.trading.tradeengine.domain.events.coin.CoinCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.coin.OrderBookAddedToCoinEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

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

    public Coin(CoinId coinId, String name, BigMoney value, BigMoney amountOfShares) {
        apply(new CoinCreatedEvent(coinId, name, value, amountOfShares));
    }

    public void addOrderBook(OrderBookId orderBookId, CurrencyPair currencyPair) {
        apply(new OrderBookAddedToCoinEvent(coinId, orderBookId, currencyPair));
    }

    @Override
    public CoinId getIdentifier() {
        return this.coinId;
    }

    @EventHandler
    public void handle(CoinCreatedEvent event) {
        this.coinId = event.getCoinIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coin coin = (Coin) o;

        if (coinId != null ? !coinId.equals(coin.coinId) : coin.coinId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return coinId != null ? coinId.hashCode() : 0;
    }
}
