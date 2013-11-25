/*
 * Copyright (c) 2012. Axon Framework
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

package com.icoin.trading.coin.event;

import com.icoin.trading.api.orders.trades.OrderBookId;
import com.icoin.trading.coin.domain.CoinId;

/**
 * <p>A new OrderBook is added to the Coin</p>
 *
 * @author Jettro Coenradie
 */
public class OrderBookAddedToCoinEvent {
    private CoinId coinId;
    private OrderBookId orderBookId;

    public OrderBookAddedToCoinEvent(CoinId coinId, OrderBookId orderBookId) {
        this.coinId = coinId;
        this.orderBookId = orderBookId;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }
}
