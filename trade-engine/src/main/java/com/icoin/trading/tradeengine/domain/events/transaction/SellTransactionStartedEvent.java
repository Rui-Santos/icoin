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

package com.icoin.trading.tradeengine.domain.events.transaction;

import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class SellTransactionStartedEvent extends AbstractTransactionStartedEvent {

    public SellTransactionStartedEvent(TransactionId transactionIdentifier,
                                       CoinId coinId,
                                       OrderBookId orderBookIdentifier,
                                       PortfolioId portfolioIdentifier,
                                       BigMoney totalItem,
                                       BigMoney pricePerItem,
                                       BigMoney totalMoney,
                                       BigMoney totalCommission) {
        super(transactionIdentifier, coinId, orderBookIdentifier, portfolioIdentifier, totalItem, pricePerItem, totalMoney, totalCommission);
    }
}
