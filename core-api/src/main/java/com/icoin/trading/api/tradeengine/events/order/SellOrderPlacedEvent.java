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

package com.icoin.trading.api.tradeengine.events.order;

import com.icoin.trading.api.coin.events.CurrencyPair;
import com.icoin.trading.api.tradeengine.events.order.OrderBookId;
import com.icoin.trading.api.tradeengine.events.order.OrderId;
import com.icoin.trading.api.tradeengine.events.portfolio.PortfolioId;
import com.icoin.trading.api.tradeengine.events.transaction.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * <p>A new Sell Order is placed for the current OrderBook. </p>
 *
 * @author Allard Buijze
 */
public class SellOrderPlacedEvent extends AbstractOrderPlacedEvent<SellOrderPlacedEvent> {

    public SellOrderPlacedEvent(OrderBookId orderBookId,
                                OrderId orderId,
                                TransactionId transactionId,
                                BigMoney tradeAmount,
                                BigMoney itemPrice,
                                BigMoney totalCommission,
                                PortfolioId portfolioId,
                                CurrencyPair currencyPair,
                                Date placeDate) {
        super(orderBookId, orderId, transactionId, tradeAmount, itemPrice, totalCommission, portfolioId, currencyPair, placeDate);
    }
}
