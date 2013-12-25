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

import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * <p>Create a new Buy Order.</p>
 *
 * @author Allard Buijze
 */
public class CreateBuyOrderCommand extends CreateOrderCommand {

    public CreateBuyOrderCommand(OrderId orderId,
                                 PortfolioId portfolioId,
                                 OrderBookId orderBookId,
                                 TransactionId transactionId,
                                 BigMoney tradeCount,
                                 BigMoney itemPrice,
                                 BigMoney totalCommission,
                                 Date placeDate) {
        super(orderId, portfolioId, orderBookId, transactionId, tradeCount, itemPrice, totalCommission, placeDate);
    }
}
