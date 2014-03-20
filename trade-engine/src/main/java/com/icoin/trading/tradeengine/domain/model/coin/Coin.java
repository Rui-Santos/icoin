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

import com.homhon.base.domain.Identity;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.events.coin.CoinCreatedEvent;
import com.icoin.trading.api.tradeengine.events.coin.OrderBookAddedToCoinEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class Coin extends AxonAnnotatedAggregateRoot<Coin, CoinId> {
    private static final long serialVersionUID = -3803633028358233820L;

    @Identity
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
}
