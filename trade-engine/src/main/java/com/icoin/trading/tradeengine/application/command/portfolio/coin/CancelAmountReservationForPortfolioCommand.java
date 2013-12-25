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

package com.icoin.trading.tradeengine.application.command.portfolio.coin;

import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

/**
 * Cancel a reservation for an amount of items for the OrderBook belonging to the provided identifier in the Portfolio
 * of the provided identifier.
 *
 * @author Jettro Coenradie
 */
public class CancelAmountReservationForPortfolioCommand {

    private PortfolioId portfolioIdentifier;
    private CoinId coinId;
    private TransactionId transactionIdentifier;
    private BigMoney amountOfCancelledItem;

    public CancelAmountReservationForPortfolioCommand(PortfolioId portfolioIdentifier,
                                                      CoinId coinId,
                                                      TransactionId transactionIdentifier,
                                                      BigMoney amountOfCancelledItem) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.coinId = coinId;
        this.transactionIdentifier = transactionIdentifier;
        this.amountOfCancelledItem = amountOfCancelledItem;
    }

    public BigMoney getAmountOfItemsToCancel() {
        return amountOfCancelledItem;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public BigMoney getAmountOfCancelledItem() {
        return amountOfCancelledItem;
    }
}
