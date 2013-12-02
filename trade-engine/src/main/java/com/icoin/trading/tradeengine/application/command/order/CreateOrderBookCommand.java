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

package com.icoin.trading.tradeengine.application.command.order;


import com.icoin.trading.tradeengine.domain.model.coin.CoinExchangePair;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;

/**
 * <p>Create a new OrderBook for the Coin represented by the provided coinIdentifier.</p>
 *
 * @author Jettro Coenradie
 */
public class CreateOrderBookCommand {
    private CoinId coinId;
    private OrderBookId orderBookId;
    private CoinExchangePair coinExchangePair;

    public CreateOrderBookCommand(OrderBookId orderBookId, CoinId coinId, CoinExchangePair coinExchangePair) {
        this.orderBookId = orderBookId;
        this.coinId = coinId;
        this.coinExchangePair = coinExchangePair;
    }

    public OrderBookId getOrderBookIdentifier() {
        return this.orderBookId;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public CoinExchangePair getCoinExchangePair() {
        return coinExchangePair;
    }
}
